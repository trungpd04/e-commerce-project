package com.nhom7.ecommercebackend.request.login;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperties;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Request object for exchanging tokens")
public class ExchangeTokenRequest {

    @Schema(description = "The client ID of the application", example = "your-client-id", required = true)
    private String clientId;

    @Schema(description = "The client secret of the application", example = "your-client-secret", required = true)
    private String clientSecret;

    @Schema(description = "The authorization code received from the authorization server", example = "authorization-code", required = true)
    private String code;

    @Schema(description = "The grant type (e.g., 'authorization_code')", example = "authorization_code", required = true)
    private String grantType;

    private String responseType;

    @Schema(description = "The redirect URI to which the response will be sent", example = "https://yourapp.com/callback", required = true)
    private String redirectUri;
}
