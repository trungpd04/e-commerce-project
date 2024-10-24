package com.nhom7.ecommercebackend.utils;

import com.nhom7.ecommercebackend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUtil {
    private final UserDetailsService userDetailsService;
    public User getLoggedInUser() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return (User) userDetailsService.loadUserByUsername(name);
    }
}
