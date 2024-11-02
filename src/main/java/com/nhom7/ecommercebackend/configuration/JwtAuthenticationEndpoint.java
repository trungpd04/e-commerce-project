package com.nhom7.ecommercebackend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
@Component
@Deprecated
public class JwtAuthenticationEndpoint implements AuthenticationEntryPoint {
    private final HandlerExceptionResolver resolver;


    public JwtAuthenticationEndpoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        List<String> error = Collections.singletonList(authException.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(error)
                .status(response.getStatus())
                .path(request.getContextPath())
                .timeStamp(new Date())
                .build();
        this.resolver.resolveException(request, response, errorResponse, authException);
    }
}
