package com.nhom7.ecommercebackend.response.login;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponse {

    @JsonProperty("access_token")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String accessToken;
    private boolean authenticated;
}
