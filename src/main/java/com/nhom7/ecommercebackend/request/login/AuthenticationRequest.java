package com.nhom7.ecommercebackend.request.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder({"user_name", "password"})
public class AuthenticationRequest {

    @JsonProperty("user_name")
    @NotNull(message = "User's name could not be null!")
    private String userName;

    @NotNull(message = "Password could not be null!")
    private String password;
}
