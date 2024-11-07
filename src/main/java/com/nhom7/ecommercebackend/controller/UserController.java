package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.exception.PasswordCreationException;
import com.nhom7.ecommercebackend.exception.PermissionDenyException;
import com.nhom7.ecommercebackend.exception.TokenException;
import com.nhom7.ecommercebackend.exception.UnsupportedLoginException;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.request.login.AuthenticationRequest;
import com.nhom7.ecommercebackend.request.login.IntrospectRequest;
import com.nhom7.ecommercebackend.request.login.LogoutRequest;
import com.nhom7.ecommercebackend.request.token.RefreshTokenDTO;
import com.nhom7.ecommercebackend.request.user.*;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.response.login.AuthenticationResponse;
import com.nhom7.ecommercebackend.response.order.OrderListResponse;
import com.nhom7.ecommercebackend.response.order.OrderResponse;
import com.nhom7.ecommercebackend.response.user.UserDetailResponse;
import com.nhom7.ecommercebackend.response.user.UserListResponse;
import com.nhom7.ecommercebackend.service.AuthenticateService;
import com.nhom7.ecommercebackend.service.EmailService;
import com.nhom7.ecommercebackend.service.OrderService;
import com.nhom7.ecommercebackend.service.UserService;
import com.nhom7.ecommercebackend.utils.FileUtils;
import com.nhom7.ecommercebackend.utils.SecurityUtils;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.text.ParseException;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.springframework.http.HttpStatus.*;


@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticateService authenticateService;
    private final UserService userService;
    private final EmailService emailService;
    private final OrderService orderService;
    private final SecurityUtils userUtil;

    @Value("${app.cors.allowedOrigins}")
    private String ALLOW_ORIGIN;

    @PostMapping("/register")
    public ApiResponse registerUser(@RequestBody UserDTO userDTO) throws PermissionDenyException, PasswordCreationException {
        User newUser = userService.register(userDTO);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Register User successfully!")
                .data(UserDetailResponse.fromUser(newUser))
                .build();
    }
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @PutMapping("")
    public ApiResponse updateUser(@RequestBody UpdateUserDTO userDTO) throws PermissionDenyException, TokenException, ParseException, JOSEException {
        User loggedInUser = userUtil.getLoggedInUser();
        User updatedUser = userService.updateUser(loggedInUser, userDTO);
        authenticateService.clearSecurity();
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Update User information successfully!")
                .data(UserDetailResponse.fromUser(updatedUser))
                .build();
    }
    @PostMapping(value = "/upload_profile_image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse uploadProfileImage(
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        User loginUser = userUtil.getLoggedInUser();
        if (file == null || file.isEmpty()) {
            return
                    ApiResponse.builder()
                            .message("Image file is required.")
                            .status(BAD_REQUEST.value())
                            .build();
        }

        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            return ApiResponse.builder()
                            .message("Image file size exceeds the allowed limit of 10MB.")
                            .status(PAYLOAD_TOO_LARGE.value())
                            .build();
        }


        // Store file and get filename
        String oldFileName = loginUser.getProfileImage();
        String imageName = FileUtils.storeFile(file);

        userService.changeProfileImage(loginUser.getId(), imageName);
        // Delete old file if exists
        if (!StringUtils.isEmpty(oldFileName)) {
            FileUtils.deleteFile(oldFileName);
        }
        return ApiResponse.builder()
                .message("Upload profile image successfully")
                .status(CREATED.value())
                .data(imageName) // Return the filename or image URL
                .build();
    }
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/profile_images/{imageName}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            java.nio.file.Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/default-profile-image.jpeg").toUri()));
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/my-order")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse getMyOrder(
            @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword,
            @RequestParam(value = "size", defaultValue = "5", required = false) int size,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page
    ) {
        PageRequest request = PageRequest.of(page, size);
        Page<OrderResponse> existingOrders = orderService.getMyOrders(keyword.toLowerCase().trim(), request);
        OrderListResponse orderListResponse = OrderListResponse.builder()
                .orders(existingOrders.getContent())
                .totalPages(existingOrders.getTotalPages())
                .build();
        return ApiResponse.builder()
                .message("Get all user's order successfully!")
                .status(HTTP_OK)
                .data(orderListResponse)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/{userId}")
    public ApiResponse getUserById(@PathVariable("userId") Long userId) {
        User existinUser = userService.findUserById(userId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Get user information successfully!")
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
            ) throws PermissionDenyException, UnsupportedLoginException {
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
            @RequestBody RefreshTokenDTO refreshTokenDTO
            ) throws TokenException, ParseException, JOSEException {
        AuthenticationResponse authenticationResponse = authenticateService.refreshToken(refreshTokenDTO);
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
    public ApiResponse oauth2Login(
            @Parameter(
                    name = "login_type",
                    description = "facebook or google",
                    example = "google"
            )
            @RequestParam("login_type") String loginType
    ) {
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
            @Parameter(
                    name = "login_type",
                    description = "facebook or google",
                    example = "google"
            )
            @RequestParam("login_type") String loginType
    ) throws Exception {
        AuthenticationResponse request = authenticateService.exchangeToken(code, loginType);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Login successfully!")
                .data(request)
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
        Page<UserDetailResponse> userDetailResponses = userService.getAllUsers(keyword.toLowerCase().trim(), pageRequest);
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
    public ApiResponse createPassword(@RequestBody PasswordCreationDTO request) throws PasswordCreationException, TokenException, ParseException, JOSEException {
        userService.createPassword(request);
        authenticateService.clearSecurity();
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Create password successfully!")
                .build();
    }
    @PostMapping("/change_password")
    @SecurityRequirement(name = "bearer-key")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse changePassword(
            @RequestBody ChangePasswordDTO dto
            ) throws PasswordCreationException, TokenException, ParseException, JOSEException {
        User user = userUtil.getLoggedInUser();
        userService.changePassword(user, dto);
        authenticateService.clearSecurity();
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Change password successfully!")
                .build();
    }
    @PostMapping("/forgot_password")
    public ApiResponse forgotPassword(
            @RequestParam(value = "email") String email
    ) throws MessagingException, UnsupportedEncodingException {
        String token = userService.updateResetPasswordToken(email);
        String link = ALLOW_ORIGIN + "/reset_password?token=" + token;
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
                    .status(BAD_REQUEST.value())
                    .message(e.getMessage())
                    .build();
        }
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Reset Password Successfully!")
                .build();
    }
}
