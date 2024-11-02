package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.exception.PermissionDenyException;
import com.nhom7.ecommercebackend.exception.TokenException;
import com.nhom7.ecommercebackend.exception.UnsupportedLoginException;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.repository.UserRepository;
import com.nhom7.ecommercebackend.request.login.AuthenticationRequest;
import com.nhom7.ecommercebackend.request.login.IntrospectRequest;
import com.nhom7.ecommercebackend.request.login.LogoutRequest;
import com.nhom7.ecommercebackend.request.token.RefreshTokenRequest;
import com.nhom7.ecommercebackend.response.login.AuthenticationResponse;
import com.nhom7.ecommercebackend.service.AuthenticateService;
import com.nhom7.ecommercebackend.utils.AuthenticationUtils;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticateServiceImpl implements AuthenticateService {

    private final AuthenticationUtils authenticationUtils;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse login(AuthenticationRequest loginRequest) throws PermissionDenyException, UnsupportedLoginException {
        Optional<User> user = Optional.empty();
        String subject = null;
        if(!loginRequest.getUserName().isBlank()) {
            Optional<User> userEmailOptional = userRepository.findByEmailAndPasswordNotNull(loginRequest.getUserName());
            if(userEmailOptional.isPresent()) {
                if(userEmailOptional.get().isActive()) {
                    if(Objects.isNull(userEmailOptional.get().getPassword())) {
                        throw new UnsupportedLoginException("Wrong password or username!");
                    }
                    user = userEmailOptional;
                    subject = user.get().getEmail();
                }else{
                    throw new PermissionDenyException("Your account has bean lock!");
                }
            } else {
                Optional<User> userPhoneOptional = userRepository.findByPhoneNumberAndPasswordNotNull(loginRequest.getUserName());
                if (userPhoneOptional.isPresent()) {
                    if (userPhoneOptional.get().isActive()) {
                        if(Objects.isNull(userPhoneOptional.get().getPassword())) {
                            throw new UnsupportedLoginException("Wrong password or username!");
                        }
                        user = userPhoneOptional;
                        subject = user.get().getPhoneNumber();
                    } else {
                        throw new PermissionDenyException("Your account has bean lock!");
                    }
                } else {
                    throw new DataNotFoundException("Wrong password or username!");
                }
            }
        }

        User existingUser = user.get();


        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(subject, loginRequest.getPassword(), existingUser.getAuthorities());

        authenticationManager.authenticate(token);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .accessToken(authenticationUtils.generateToken(existingUser))
                .userFullName(existingUser.getFullName())
                .build();
    }

    @Override
    public AuthenticationResponse introspectToken(IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        return authenticationUtils.introspectToken(introspectRequest);
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws TokenException, ParseException, JOSEException {
        return authenticationUtils.refreshToken(request);
    }


    @Override
    public void logout(LogoutRequest logoutRequest) throws TokenException, ParseException, JOSEException {
        authenticationUtils.logout(logoutRequest);
    }


    @Override
    public AuthenticationResponse exchangeToken(String code, String loginType) throws UnsupportedLoginException, UnsupportedEncodingException {
        return authenticationUtils.exchangeToken(code, loginType);
    }

    @Override
    public String getOauth2LoginURL(String loginType) {
        return authenticationUtils.generateAuthUrl(loginType);
    }

}
