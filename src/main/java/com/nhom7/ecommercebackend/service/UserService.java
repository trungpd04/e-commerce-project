package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.exception.PermissionDenyException;
import com.nhom7.ecommercebackend.model.Order;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.request.user.UserDTO;
import com.nhom7.ecommercebackend.response.order.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface UserService {
    User register(UserDTO userDTO) throws PermissionDenyException;
    User updateUser(Long userId, UserDTO userDTO);
    void deleteUser(Long userId);
    User findUserById(Long userId);
    Page<OrderResponse> getMyOrders(Long userId, PageRequest pageRequest);

}
