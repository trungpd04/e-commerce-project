package com.nhom7.ecommercebackend.request.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema
public class IntrospectRequest {
    private String token;
}
