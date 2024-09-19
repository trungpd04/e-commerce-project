package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.request.UserDTO;

public interface UserService {
    User creatUser(UserDTO userDTO);
}
