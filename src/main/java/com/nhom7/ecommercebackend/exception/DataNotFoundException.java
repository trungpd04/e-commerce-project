package com.nhom7.ecommercebackend.exception;

import lombok.Data;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String message) {
        super(message);
    }
}
