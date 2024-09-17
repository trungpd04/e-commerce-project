package com.nhom7.ecommercebackend.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Builder
@Data
public class ApiResponse {
    private int status;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;
}
