package com.nhom7.ecommercebackend.response;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.List;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String path;
    private Date timeStamp;
    private List<String> error;
}
