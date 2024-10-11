package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.exception.PermissionDenyException;
import com.nhom7.ecommercebackend.exception.TokenException;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.request.login.AuthenticationRequest;
import com.nhom7.ecommercebackend.request.login.IntrospectRequest;
import com.nhom7.ecommercebackend.request.login.LogoutRequest;
import com.nhom7.ecommercebackend.request.token.RefreshTokenRequest;
import com.nhom7.ecommercebackend.request.user.UserDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.response.login.AuthenticationResponse;
import com.nhom7.ecommercebackend.response.login.ExchangeTokenResponse;
import com.nhom7.ecommercebackend.response.order.OrderListResponse;
import com.nhom7.ecommercebackend.response.order.OrderResponse;
import com.nhom7.ecommercebackend.service.AuthenticateService;
import com.nhom7.ecommercebackend.service.UserService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticateService authenticateService;
    private final UserService userService;
    @PostMapping("")
    public ApiResponse registerUser(@RequestBody UserDTO userDTO) throws PermissionDenyException {
        User newUser = userService.register(userDTO);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Register User successfully!")
                .data(newUser)
                .build();
    }
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @PutMapping("/{userId}")
    public ApiResponse updateUser(@PathVariable("userId") Long userId, @RequestBody UserDTO userDTO) throws PermissionDenyException {
        User updatedUser = userService.updateUser(userId, userDTO);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Register User successfully!")
                .data(updatedUser)
                .build();
    }
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/{userId}")
    public ApiResponse getUserById(@PathVariable("userId") Long userId) {
        User existinUser = userService.findUserById(userId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Update User successfully!")
                .data(existinUser)
                .build();
    }
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse deactivateUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return ApiResponse.builder()
                .data(HTTP_OK)
                .message("Deactivate User successfully!")
                .build();
    }
    @PostMapping("/login")
    public ApiResponse login(
            @RequestBody AuthenticationRequest loginRequest
            ) throws PermissionDenyException {
        AuthenticationResponse loginResponse = authenticateService.login(loginRequest);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Login successfully!")
                .data(loginResponse)
                .build();
    }
    @PostMapping("/logout")
    public ApiResponse logout(
            @RequestBody LogoutRequest logoutRequest
            ) throws TokenException, ParseException, JOSEException {
        authenticateService.logout(logoutRequest);
        return ApiResponse.builder().status(HTTP_OK)
                .message("Logout successfully")
                .build();
    }
    @PostMapping("/refresh")
    public ApiResponse refreshToken(
            @RequestBody RefreshTokenRequest refreshTokenRequest
            ) throws TokenException, ParseException, JOSEException {
        AuthenticationResponse authenticationResponse = authenticateService.refreshToken(refreshTokenRequest);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Refresh token successfully!")
                .data(authenticationResponse)
                .build();
    }
    @PostMapping("/introspect")
    public AuthenticationResponse introspect(
            @RequestBody IntrospectRequest introspectRequest
    ) throws ParseException, JOSEException {
        return authenticateService.introspectToken(introspectRequest);
    }
    @PostMapping("/outbound/login")
    public ApiResponse exchangeToken(
            @RequestParam String code
    ) {
        ExchangeTokenResponse response = authenticateService.exchangeToken(code);
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .accessToken(response.getAccessToken())
                .build();
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Login successfully!")
                .data(authenticationResponse)
                .build();
    }
    @GetMapping("/orders/{userId}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-token")
    public ApiResponse getMyOrders
            (
                    @PathVariable("userId") Long userId,
                    @RequestParam(value = "size", defaultValue = "5", required = false) int size,
                    @RequestParam(value = "page", defaultValue = "0", required = false) int page
            ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<OrderResponse> orderResponsePage = userService.getMyOrders(userId, pageRequest);
        OrderListResponse orderListResponse = OrderListResponse.builder()
                .orders(orderResponsePage.getContent())
                .totalPages(orderResponsePage.getTotalPages())
                .build();
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Get all orders successfully!")
                .data(orderListResponse)
                .build();
    }
}
