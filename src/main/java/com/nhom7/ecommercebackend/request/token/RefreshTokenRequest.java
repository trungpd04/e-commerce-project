package com.nhom7.ecommercebackend.request.token;

import lombok.*;

@Builder
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class RefreshTokenRequest {
    private String token;
}
