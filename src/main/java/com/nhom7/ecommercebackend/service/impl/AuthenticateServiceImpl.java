package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.exception.PermissionDenyException;
import com.nhom7.ecommercebackend.exception.TokenException;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.model.UserOauth2Info;
import com.nhom7.ecommercebackend.repository.UserRepository;
import com.nhom7.ecommercebackend.request.login.AuthenticationRequest;
import com.nhom7.ecommercebackend.request.login.IntrospectRequest;
import com.nhom7.ecommercebackend.request.login.LogoutRequest;
import com.nhom7.ecommercebackend.request.token.RefreshTokenRequest;
import com.nhom7.ecommercebackend.request.user.UserDTO;
import com.nhom7.ecommercebackend.response.login.AuthenticationResponse;
import com.nhom7.ecommercebackend.response.login.ExchangeTokenResponse;
import com.nhom7.ecommercebackend.service.AuthenticateService;
import com.nhom7.ecommercebackend.utils.JwtUtils;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticateServiceImpl implements AuthenticateService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    @Override
    public AuthenticationResponse login(AuthenticationRequest loginRequest) throws PermissionDenyException {
        Optional<User> user = Optional.empty();
        String subject = null;
        if(!loginRequest.getUserName().isBlank()) {
            Optional<User> userEmailOptional = userRepository.findByEmail(loginRequest.getUserName());
            if(userEmailOptional.isPresent()) {
                if(userEmailOptional.get().isActive()) {
                    user = userEmailOptional;
                    subject = user.get().getEmail();
                }else{
                    throw new PermissionDenyException("Your account has bean lock!");
                }
            }else {
                Optional<User> userPhoneOptional = userRepository.findByPhoneNumber(loginRequest.getUserName());
                if (userPhoneOptional.isPresent()) {
                    if (userPhoneOptional.get().isActive()) {
                        user = userPhoneOptional;
                        subject = user.get().getPhoneNumber();
                    } else {
                        throw new PermissionDenyException("Your account has bean lock!");
                    }
                } else {
                    throw new DataNotFoundException("Your account does not exits!");
                }
            }
        }
        User existiongUser = user.get();
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(subject, loginRequest.getPassword(), existiongUser.getAuthorities());
        System.out.println(authenticationToken.getAuthorities().toString());
        authenticationManager.authenticate(authenticationToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtUtils.generateToken(existiongUser))
                .authenticated(true)
                .build();
    }

    @Override
    public AuthenticationResponse introspectToken(IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        return jwtUtils.introspectToken(introspectRequest);
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws TokenException, ParseException, JOSEException {
        return jwtUtils.refreshToken(request);
    }

    @Override
    public UserOauth2Info getOauth2UserInfo(String alt, String accessToken) {
        return jwtUtils.getOauth2UserInfo(alt, accessToken) ;
    }

    @Override
    public void logout(LogoutRequest logoutRequest) throws TokenException, ParseException, JOSEException {
        jwtUtils.logout(logoutRequest);
    }

    @Override
    public User register(UserDTO userDTO) {
        return null;
    }

    @Override
    public ExchangeTokenResponse exchangeToken(String code) {
        return jwtUtils.exchangeToken(code);
    }
}
