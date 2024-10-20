package com.nhom7.ecommercebackend.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
public class ResetPasswordDTO {
    private String password;

    @JsonProperty("retype_password")
    private String retypePassword;
}
