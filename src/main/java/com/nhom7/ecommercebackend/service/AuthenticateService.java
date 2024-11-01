package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.exception.PermissionDenyException;
import com.nhom7.ecommercebackend.exception.TokenException;
import com.nhom7.ecommercebackend.exception.UnsupportedLoginException;
import com.nhom7.ecommercebackend.response.login.UserOauth2Info;
import com.nhom7.ecommercebackend.request.login.AuthenticationRequest;
import com.nhom7.ecommercebackend.request.login.IntrospectRequest;
import com.nhom7.ecommercebackend.request.login.LogoutRequest;
import com.nhom7.ecommercebackend.request.token.RefreshTokenRequest;
import com.nhom7.ecommercebackend.response.login.AuthenticationResponse;
import com.nhom7.ecommercebackend.response.login.ExchangeTokenResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticateService {
    AuthenticationResponse login(AuthenticationRequest loginRequest) throws PermissionDenyException;
    AuthenticationResponse introspectToken(IntrospectRequest introspectRequest) throws ParseException, JOSEException;
    AuthenticationResponse refreshToken(RefreshTokenRequest request) throws TokenException, ParseException, JOSEException;
    void logout(LogoutRequest logoutRequest) throws TokenException, ParseException, JOSEException;
    AuthenticationRequest exchangeToken(String code, String loginType) throws UnsupportedLoginException;
    String getOauth2LoginURL(String loginType);
}
