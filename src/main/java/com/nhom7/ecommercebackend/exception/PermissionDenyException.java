package com.nhom7.ecommercebackend.exception;

import org.springframework.security.core.parameters.P;

public class PermissionDenyException extends Exception{
    public PermissionDenyException(String message) {
        super(message);
    }
}
