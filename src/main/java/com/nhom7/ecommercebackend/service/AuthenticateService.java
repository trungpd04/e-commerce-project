package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.exception.PermissionDenyException;
import com.nhom7.ecommercebackend.exception.TokenException;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.model.UserOauth2Info;
import com.nhom7.ecommercebackend.request.login.AuthenticationRequest;
import com.nhom7.ecommercebackend.request.login.IntrospectRequest;
import com.nhom7.ecommercebackend.request.login.LogoutRequest;
import com.nhom7.ecommercebackend.request.token.RefreshTokenRequest;
import com.nhom7.ecommercebackend.request.user.UserDTO;
import com.nhom7.ecommercebackend.response.login.AuthenticationResponse;
import com.nhom7.ecommercebackend.response.login.ExchangeTokenResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticateService {
    AuthenticationResponse login(AuthenticationRequest loginRequest) throws PermissionDenyException;
    AuthenticationResponse introspectToken(IntrospectRequest introspectRequest) throws ParseException, JOSEException;
    AuthenticationResponse refreshToken(RefreshTokenRequest request) throws TokenException, ParseException, JOSEException;
    UserOauth2Info getOauth2UserInfo(String alt, String accessToken);
    void logout(LogoutRequest logoutRequest) throws TokenException, ParseException, JOSEException;
    User register(UserDTO userDTO);
    ExchangeTokenResponse exchangeToken(String code);
}
