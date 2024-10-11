package com.nhom7.ecommercebackend.request.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationRequest {

    @JsonProperty("user_name")
    private String userName;
    private String password;
}
