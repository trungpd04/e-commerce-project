package com.nhom7.ecommercebackend.response.login;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserOauth2Info {

    private String id;
    private String email;
    private boolean verifiedEmail;
    private String name;
    private String givenName;
    private String familyName;
    private Object picture;

}
