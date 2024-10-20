package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.exception.PasswordCreationException;
import com.nhom7.ecommercebackend.exception.PermissionDenyException;
import com.nhom7.ecommercebackend.exception.TokenException;
import com.nhom7.ecommercebackend.model.Order;
import com.nhom7.ecommercebackend.model.ResetPasswordToken;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.request.user.PasswordCreationRequest;
import com.nhom7.ecommercebackend.request.user.ResetPasswordDTO;
import com.nhom7.ecommercebackend.request.user.UserDTO;
import com.nhom7.ecommercebackend.response.order.OrderResponse;
import com.nhom7.ecommercebackend.response.user.UserDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface UserService {
    User register(UserDTO userDTO) throws PermissionDenyException, PasswordCreationException;
    User updateUser(Long userId, UserDTO userDTO);
    void deleteUser(Long userId);
    User findUserById(Long userId);
    Page<UserDetailResponse> getAllUsers(String keyword, PageRequest pageRequest);
    Page<OrderResponse> getMyOrders(Long userId, PageRequest pageRequest);
    UserDetailResponse getUserDetail();
    void createPassword(PasswordCreationRequest request) throws PasswordCreationException;
    String updateResetPasswordToken(String email);
    User getByResetPasswordToken(String token) throws TokenException;
    void updatePassword(User user, ResetPasswordDTO dto) throws TokenException, PasswordCreationException;
}
