package com.nhom7.ecommercebackend.utils;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.nhom7.ecommercebackend.configuration.RsaKeyProperties;
import com.nhom7.ecommercebackend.exception.MessageKeys;
import com.nhom7.ecommercebackend.exception.TokenException;
import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.exception.UnsupportedLoginException;
import com.nhom7.ecommercebackend.model.AuthProvider;
import com.nhom7.ecommercebackend.model.InvalidatedToken;
import com.nhom7.ecommercebackend.model.Role;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.response.login.UserOauth2Info;
import com.nhom7.ecommercebackend.repository.InvalidatedTokenRepository;
import com.nhom7.ecommercebackend.repository.RoleRepository;
import com.nhom7.ecommercebackend.repository.outbound.FacebookLoginClient;
import com.nhom7.ecommercebackend.repository.outbound.FacebookUserInfoClient;
import com.nhom7.ecommercebackend.repository.outbound.GoogleLoginClient;
import com.nhom7.ecommercebackend.repository.UserRepository;
import com.nhom7.ecommercebackend.repository.outbound.GoogleUserInfoClient;
import com.nhom7.ecommercebackend.request.login.ExchangeTokenRequest;
import com.nhom7.ecommercebackend.request.login.IntrospectRequest;
import com.nhom7.ecommercebackend.request.login.LogoutRequest;
import com.nhom7.ecommercebackend.request.token.RefreshTokenDTO;
import com.nhom7.ecommercebackend.response.login.AuthenticationResponse;
import com.nhom7.ecommercebackend.response.login.ExchangeTokenResponse;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@RequiredArgsConstructor
public class AuthenticationUtils {

    private final RsaKeyProperties rsaKeyProperties;
    private final JwtEncoder jwtEncoder;
    private final InvalidatedTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final GoogleLoginClient googleLoginClient;
    private final GoogleUserInfoClient googleUserInfoClient;
    private final FacebookLoginClient facebookLoginClient;
    private final FacebookUserInfoClient facebookUserInfoClient;

    private final RoleRepository roleRepository;

    @Value("${jwt.expiration_time}")
    protected Long EXPIRATION_TIME;

    @Value("${jwt.refresh_duration}")
    protected Long REFRESH_DURATION;

    @NonFinal
    @Value("${oauth2.google.client_id}")
    protected String GOOGLE_CLIENT_ID;

    @NonFinal
    @Value("${oauth2.google.client_secret}")
    protected String GOOGLE_CLIENT_SECRET;

    @NonFinal
    @Value("${oauth2.google.grant_type}")
    protected String GOOGLE_GRANT_TYPE;

    @NonFinal
    @Value("${oauth2.google.redirect_uri}")
    protected String GOOGLE_REDIRECT_URI;

    @NonFinal
    @Value("${oauth2.facebook.client_id}")
    protected String FACEBOOK_CLIENT_ID;

    @NonFinal
    @Value("${oauth2.facebook.client_secret}")
    protected String FACEBOOK_CLIENT_SECRET;

    @NonFinal
    @Value("${oauth2.facebook.auth_uri}")
    protected String FACEBOOK_AUTH_URI;

    @NonFinal
    @Value("${oauth2.facebook.grant_type}")
    protected String FACEBOOK_RESPONSE_TYPE;

    @NonFinal
    @Value("${oauth2.facebook.redirect_uri}")
    protected String FACEBOOK_REDIRECT_URI;

    public AuthenticationResponse introspectToken(IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        try {
            verifyToken(false, introspectRequest.getToken());
        } catch (TokenException e) {
            return AuthenticationResponse.builder().authenticated(false).build();
        }
        return AuthenticationResponse.builder().authenticated(true).build();
    }
    public String generateToken(User user) {
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())
                .subject(getSubject(user))
                .issuer("nhom7")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(EXPIRATION_TIME, ChronoUnit.SECONDS))
                .claim("scope", buildRole(user))
                .build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }
    public String generateTokenForOauth2User(User user) {
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getProviderId())
                .issuer("nhom7")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(EXPIRATION_TIME, ChronoUnit.SECONDS))
                .claim("scope", buildRole(user))
                .build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

    private String buildRole(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getAuthorities())) {
            user.getAuthorities().forEach(role -> {
                stringJoiner.add(role.getAuthority());
            });
        }
        return stringJoiner.toString();
    }
    public AuthenticationResponse refreshToken(RefreshTokenDTO refreshTokenDTO) throws TokenException, ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(true, refreshTokenDTO.getToken());

        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();

        String subject = signedJWT.getJWTClaimsSet().getSubject();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jwtId)
                .expiryTime(signedJWT.getJWTClaimsSet().getExpirationTime())
                .build();
        tokenRepository.save(invalidatedToken);

        User user = getUser(subject);
        String token = generateToken(user);
        return AuthenticationResponse.builder()
                .accessToken(token)
                .authenticated(true)
                .build();
    }
    public void logout(LogoutRequest logoutRequest) throws TokenException, ParseException, JOSEException {

            var signToken = verifyToken(true, logoutRequest.getAccessToken());

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

            tokenRepository.save(invalidatedToken);

    }
    public SignedJWT verifyToken(boolean isRefresh, String token) throws ParseException, JOSEException, TokenException {

        JWSVerifier jwsVerifier = new RSASSAVerifier(rsaKeyProperties.rsaPublicKey());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expireDate = (isRefresh) ?
                new Date(signedJWT.getJWTClaimsSet()
                        .getIssueTime()
                        .toInstant()
                        .plus(REFRESH_DURATION, ChronoUnit.SECONDS)
                        .toEpochMilli())
                        : signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(jwsVerifier);

        if(!( verified && expireDate.after(new Date()))) {
            throw new TokenException(MessageKeys.INVALID_TOKEN.toString());
        }
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        if(tokenRepository.existsById(jwtId)) {
            throw new TokenException(MessageKeys.UNAUTHENTICATED.toString());
        }
        return signedJWT;
    }
    public AuthenticationResponse exchangeToken(String code, String loginType) throws UnsupportedLoginException, UnsupportedEncodingException {
        loginType = loginType.trim().toLowerCase();

        switch(loginType) {
            case "google": {
                String result = java.net.URLDecoder.decode(code, StandardCharsets.UTF_8);
                ExchangeTokenRequest request = ExchangeTokenRequest.builder()
                        .clientId(GOOGLE_CLIENT_ID)
                        .clientSecret(GOOGLE_CLIENT_SECRET)
                        .grantType(GOOGLE_GRANT_TYPE)
                        .redirectUri(GOOGLE_REDIRECT_URI)
                        .code(result)
                        .build();

                ExchangeTokenResponse exchangeTokenResponse = googleLoginClient
                        .exchangeToken(request);
                Role role = roleRepository.findByName(Role.USER);

                UserOauth2Info userOauth2Info = getGoogleOauth2UserInfo("json", exchangeTokenResponse.getAccessToken());

                User user = userRepository.findByProviderAndProviderId(AuthProvider.google, userOauth2Info.getId())
                        .orElseGet(() -> userRepository.save(User.builder()
                                .email(userOauth2Info.getEmail())
                                .fullName(userOauth2Info.getFamilyName() + " " + userOauth2Info.getGivenName())
                                .profileImage(userOauth2Info.getPicture().toString())
                                .active(true)
                                .providerId(userOauth2Info.getId())
                                .provider(AuthProvider.google)
                                .role(role)
                                .build()));

                return AuthenticationResponse.builder()
                        .userFullName(user.getFullName())
                        .accessToken(generateTokenForOauth2User(user))
                        .authenticated(true)
                        .build();
            }
            case "facebook": {
                String result = java.net.URLDecoder.decode(code, StandardCharsets.UTF_8);
                ExchangeTokenRequest request = ExchangeTokenRequest.builder()
                        .clientId(FACEBOOK_CLIENT_ID)
                        .clientSecret(FACEBOOK_CLIENT_SECRET)
                        .responseType(FACEBOOK_RESPONSE_TYPE)
                        .redirectUri(FACEBOOK_REDIRECT_URI)
                        .code(result)
                        .build();
                ExchangeTokenResponse exchangeTokenResponse = facebookLoginClient
                        .exchangeToken(request);
                Role role = roleRepository.findByName(Role.USER);
                UserOauth2Info userOauth2Info = getFacebookOauth2UserInfo(exchangeTokenResponse.getAccessToken());
                Object pictureObj = userOauth2Info.getPicture();
                String picture;
                if (pictureObj instanceof Map<?, ?> pictureData) {
                    Object dataObj = pictureData.get("data");
                    if (dataObj instanceof Map<?, ?> dataMap) {
                        Object urlObj = dataMap.get("url");
                        if (urlObj instanceof String) {
                            picture = (String) urlObj;
                        } else {
                            picture = null;
                        }
                    } else {
                        picture = null;
                    }
                } else {
                    picture = null;
                }
                User user = userRepository.findByProviderAndProviderId(AuthProvider.facebook, userOauth2Info.getId())
                        .orElseGet(() -> userRepository.save(User.builder()
                                .email(userOauth2Info.getEmail())
                                .fullName(userOauth2Info.getName())
                                .profileImage(picture)
                                .active(true)
                                .provider(AuthProvider.facebook)
                                .providerId(userOauth2Info.getId())
                                .role(role)
                                .build()));

                return AuthenticationResponse.builder()
                        .userFullName(user.getFullName())
                        .accessToken(generateTokenForOauth2User(user))
                        .authenticated(true)
                        .build();
            }
            default:
                throw new UnsupportedLoginException("Unsupported login type: " + loginType);
        }
    }
    private String getSubject(User user) {
        if(user.getEmail() != null) {
            return user.getEmail();
        }else{
            return user.getPhoneNumber();
        }
    }
    private User getUser(String subject) {
        Optional<User> existingUserWithEmail = userRepository.findByEmailAndPasswordNotNull(subject);
        if(existingUserWithEmail.isPresent()) {
            return existingUserWithEmail.get();
        }
        Optional<User> existingUserWithPhoneNumber = userRepository.findByPhoneNumberAndPasswordNotNull(subject);
        if(existingUserWithPhoneNumber.isPresent()) {
            return existingUserWithPhoneNumber.get();
        }
        throw new DataNotFoundException(MessageKeys.USER_NOT_EXIST.toString());
    }
    public UserOauth2Info getGoogleOauth2UserInfo(String alt, String accessToken) {
        return googleUserInfoClient.userInfo(alt, accessToken);
    }
    public UserOauth2Info getFacebookOauth2UserInfo(String accessToken) {
        return facebookUserInfoClient.userInfo(accessToken);
    }
    public String generateAuthUrl(String loginType) {
        String url = "";

        loginType = loginType.trim().toLowerCase(); // Normalize the login type

        if ("google".equals(loginType)) {
            GoogleAuthorizationCodeRequestUrl urlBuilder = new GoogleAuthorizationCodeRequestUrl(
                    GOOGLE_CLIENT_ID,
                    GOOGLE_REDIRECT_URI,
                    Arrays.asList("email", "profile", "openid"));
            url = urlBuilder.build();
        } else if ("facebook".equals(loginType)) {
            url = UriComponentsBuilder
                    .fromUriString(FACEBOOK_AUTH_URI)
                    .queryParam("client_id", FACEBOOK_CLIENT_ID)
                    .queryParam("redirect_uri", FACEBOOK_REDIRECT_URI)
                    .queryParam("scope", "email,public_profile")
                    .queryParam("response_type", FACEBOOK_RESPONSE_TYPE)
                    .build()
                    .toUriString();
        }

        return url;
    }
}
