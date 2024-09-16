package com.nhom7.ecommercebackend.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.model.AuthProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {

    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber = "";

    @JsonProperty("email")
    private String email = "";

    private String address = "";

    @NotBlank(message = "Password cannot be blank")
    private String password = "";

    @JsonProperty("retype_password")
    private String retypePassword = "";

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    private String provider;

    @JsonProperty("provider_id")
    private String providerId;

    @JsonProperty("profile_image")
    private String profileImage;


    @NotNull(message = "Role ID is required")
    @JsonProperty("role_id")
    //role admin not permitted
    private Long roleId;
}