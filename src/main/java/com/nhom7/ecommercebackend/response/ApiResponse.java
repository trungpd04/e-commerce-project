package com.nhom7.ecommercebackend.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Builder
@Data
public class ApiResponse {
    private int status;
    private String message;
    private Object data;
}
