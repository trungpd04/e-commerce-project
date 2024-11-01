package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.exception.PasswordCreationException;
import com.nhom7.ecommercebackend.exception.PermissionDenyException;
import com.nhom7.ecommercebackend.exception.TokenException;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.request.login.AuthenticationRequest;
import com.nhom7.ecommercebackend.request.login.IntrospectRequest;
import com.nhom7.ecommercebackend.request.login.LogoutRequest;
import com.nhom7.ecommercebackend.request.token.RefreshTokenRequest;
import com.nhom7.ecommercebackend.request.user.PasswordCreationRequest;
import com.nhom7.ecommercebackend.request.user.ResetPasswordDTO;
import com.nhom7.ecommercebackend.request.user.UserDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.response.login.AuthenticationResponse;
import com.nhom7.ecommercebackend.response.login.ExchangeTokenResponse;
import com.nhom7.ecommercebackend.response.order.OrderListResponse;
import com.nhom7.ecommercebackend.response.order.OrderResponse;
import com.nhom7.ecommercebackend.response.user.UserDetailResponse;
import com.nhom7.ecommercebackend.response.user.UserListResponse;
import com.nhom7.ecommercebackend.service.AuthenticateService;
import com.nhom7.ecommercebackend.service.EmailService;
import com.nhom7.ecommercebackend.service.UserService;
import com.nimbusds.jose.JOSEException;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticateService authenticateService;
    private final UserService userService;
    private final EmailService emailService;

    @Value("${app.cors.allowedOrigins}")
    private String ALLOW_ORIGIN;

    @PostMapping("/register")
    public ApiResponse registerUser(@RequestBody UserDTO userDTO) throws PermissionDenyException, PasswordCreationException {
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
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/{userId}")
    public ApiResponse getUserById(@PathVariable("userId") Long userId) {
        User existinUser = userService.findUserById(userId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Update User successfully!")
                .data(UserDetailResponse.fromUser(existinUser))
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
    @PostMapping("/oauth2/login")
    public ApiResponse oauth2Login(@RequestParam("login_type") String loginType) {
        String url = authenticateService.getOauth2LoginURL(loginType);
        return ApiResponse.builder()
                .data(url)
                .status(HTTP_OK)
                .message("Get oath2 login URL successfully!")
                .build();
    }

    @GetMapping("/orders/{userId}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-key")
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
    @GetMapping("/oauth2/social/callback")
    public ApiResponse callback(
            @RequestParam("code") String code,
            @RequestParam("login_type") String loginType
    ) throws Exception {
        AuthenticationRequest request = authenticateService.exchangeToken(code, loginType);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Login successfully!")
                .data(this.login(request))
                .build();
    }

    @GetMapping("/detail")
    @SecurityRequirement(name = "bearer-key")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ApiResponse getUserDetail() {
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Get User's detail successfully!")
                .data(userService.getUserDetail())
                .build();
    }

    @GetMapping("")
    @SecurityRequirement(name = "bearer-key")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse getAllUser(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserDetailResponse> userDetailResponses = userService.getAllUsers(keyword, pageRequest);
        UserListResponse userListResponse = UserListResponse.builder()
                .userDetailResponses(userDetailResponses.getContent())
                .pageNo(page)
                .pageSize(size)
                .totalPages(userDetailResponses.getTotalPages())
                .totalElements(userDetailResponses.getTotalElements())
                .last(userDetailResponses.isLast())
                .build();
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Get all user successfully!")
                .data(userListResponse)
                .build();
    }
    @PostMapping("/create_password")
    @SecurityRequirement(name = "bearer-key")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse createPassword(@RequestBody PasswordCreationRequest request) throws PasswordCreationException {
        userService.createPassword(request);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Create password successfully!")
                .build();
    }
    @PostMapping("/forgot_password")
    public ApiResponse forgotPassword(@RequestParam(value = "email") String email) throws MessagingException, UnsupportedEncodingException {
        String token = userService.updateResetPasswordToken(email);
        String link = ALLOW_ORIGIN + "/reset_password?token=" + token;
        System.out.println(link);
        emailService.sendEmail(email, link);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Send reset password email successfully!")
                .build();
    }
    @PostMapping("/reset_password")
    public ApiResponse resetPassword(
            @RequestParam(value = "token") String token,
            @RequestBody ResetPasswordDTO dto
            ) throws TokenException {
        User existingUser = userService.getByResetPasswordToken(token);
        try {
            userService.updatePassword(existingUser, dto);
        } catch (TokenException | PasswordCreationException e) {
            return ApiResponse.builder()
                    .status(HTTP_BAD_REQUEST)
                    .message(e.getMessage())
                    .build();
        }
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Reset Password Successfully!")
                .build();
    }
}
