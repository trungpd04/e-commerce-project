package com.nhom7.ecommercebackend.utils;

import com.nhom7.ecommercebackend.configuration.RsaKeyProperties;
import com.nhom7.ecommercebackend.exception.MessageKeys;
import com.nhom7.ecommercebackend.exception.TokenException;
import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.InvalidatedToken;
import com.nhom7.ecommercebackend.model.Role;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.model.UserOauth2Info;
import com.nhom7.ecommercebackend.repository.InvalidatedTokenRepository;
import com.nhom7.ecommercebackend.repository.RoleRepository;
import com.nhom7.ecommercebackend.repository.outbound.OutboundLoginClient;
import com.nhom7.ecommercebackend.repository.UserRepository;
import com.nhom7.ecommercebackend.repository.outbound.OutboundUserInfoClient;
import com.nhom7.ecommercebackend.request.login.ExchangeTokenRequest;
import com.nhom7.ecommercebackend.request.login.IntrospectRequest;
import com.nhom7.ecommercebackend.request.login.LogoutRequest;
import com.nhom7.ecommercebackend.request.token.RefreshTokenRequest;
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

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtUtils {
    private final RsaKeyProperties rsaKeyProperties;
    private final JwtEncoder jwtEncoder;
    private final InvalidatedTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final OutboundLoginClient outboundLoginClient;
    private final OutboundUserInfoClient outboundUserInfoClient;
    private final RoleRepository roleRepository;

    @Value("${jwt.expiration_time}")
    protected Long EXPIRATION_TIME;

    @Value("${jwt.refresh_duration}")
    protected Long REFRESH_DURATION;

    @NonFinal
    @Value("${oauth2.google.client_id}")
    protected String CLIENT_ID;
    @NonFinal
    @Value("${oauth2.google.client_secret}")
    protected String CLIENT_SECRET;
    @NonFinal
    @Value("${oauth2.google.grant_type}")
    protected String GRANT_TYPE;
    @NonFinal
    @Value("${oauth2.google.redirect_uri}")
    protected String REDIRECT_URI;
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
    private String buildRole(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getAuthorities())) {
            user.getAuthorities().forEach(role -> {
                stringJoiner.add(role.getAuthority());
            });
        }
        return stringJoiner.toString();
    }
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) throws TokenException, ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(true, refreshTokenRequest.getToken());

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
    public ExchangeTokenResponse exchangeToken(String code) {
        ExchangeTokenRequest request = ExchangeTokenRequest.builder()
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .grantType(GRANT_TYPE)
                .redirectUri(REDIRECT_URI)
                .code(code)
                .build();
        ExchangeTokenResponse exchangeTokenResponse = outboundLoginClient
                .exchangeToken(request);
        Role role = roleRepository.findByName(Role.USER);
        UserOauth2Info userOauth2Info = getOauth2UserInfo("json", exchangeTokenResponse.getAccessToken());
        User user = userRepository.findByEmail(userOauth2Info.getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                                .email(userOauth2Info.getEmail())
                                .fullName(userOauth2Info.getFamilyName() + " " + userOauth2Info.getGivenName())
                                .profileImage(userOauth2Info.getPicture())
                                .active(true)
                                .role(role)
                        .build()));
        String token = generateToken(user);

        return ExchangeTokenResponse.builder().accessToken(token).build();
    }
    private String getSubject(User user) {
        if(user.getEmail() != null) {
            return user.getEmail();
        }else{
            return user.getPhoneNumber();
        }
    }
    private User getUser(String subject) {
        Optional<User> existingUserWithEmail = userRepository.findByEmail(subject);
        if(existingUserWithEmail.isPresent()) {
            return existingUserWithEmail.get();
        }
        Optional<User> existingUserWithPhoneNumber = userRepository.findByPhoneNumber(subject);
        if(existingUserWithPhoneNumber.isPresent()) {
            return existingUserWithPhoneNumber.get();
        }
        throw new DataNotFoundException(MessageKeys.USER_NOT_EXIST.toString());
    }
    public UserOauth2Info getOauth2UserInfo(String alt, String accessToken) {
        return outboundUserInfoClient.userInfo(alt, accessToken);
    }
}
