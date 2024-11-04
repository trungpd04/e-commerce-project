package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.exception.PasswordCreationException;
import com.nhom7.ecommercebackend.exception.PermissionDenyException;
import com.nhom7.ecommercebackend.exception.TokenException;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.request.user.*;
import com.nhom7.ecommercebackend.response.order.OrderResponse;
import com.nhom7.ecommercebackend.response.user.UserDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    User register(UserDTO userDTO) throws PermissionDenyException, PasswordCreationException;
    User updateUser(User user, UpdateUserDTO userDTO);
    void deleteUser(Long userId);
    User findUserById(Long userId);
    Page<UserDetailResponse> getAllUsers(String keyword, PageRequest pageRequest);
    Page<OrderResponse> getMyOrders(Long userId, PageRequest pageRequest);
    UserDetailResponse getUserDetail();
    void createPassword(PasswordCreationDTO request) throws PasswordCreationException;
    String updateResetPasswordToken(String email);
    User getByResetPasswordToken(String token) throws TokenException;
    void updatePassword(User user, ResetPasswordDTO dto) throws TokenException, PasswordCreationException;
    void changeProfileImage(Long userId, String image);
    void changePassword(Long userId, ChangePasswordDTO dto) throws PasswordCreationException;
}
