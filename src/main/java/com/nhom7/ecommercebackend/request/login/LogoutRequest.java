package com.nhom7.ecommercebackend.request.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Schema
public class LogoutRequest {
    @JsonProperty("access_token")
    private String accessToken;
}
