package com.nhom7.ecommercebackend.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class PasswordCreationRequest {
    private String password;
    @JsonProperty("retype_password")
    private String retypePassword;
}
