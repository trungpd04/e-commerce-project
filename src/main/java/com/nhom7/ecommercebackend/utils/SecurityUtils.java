package com.nhom7.ecommercebackend.utils;

import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserService userService;

    public User getLoggedInUser() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return (User) userService.loadUserByUsername(name);
    }
}
