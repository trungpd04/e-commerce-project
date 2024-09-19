package com.nhom7.ecommercebackend.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.model.AuthProvider;
import com.nhom7.ecommercebackend.validation.DobConstrain;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

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

    @Pattern(regexp = "^(|.+@.+\\..+)$", message = "Invalid email format")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email = "";

    private String address = "";

    @NotBlank(message = "Password cannot be blank")
    @Length(min = 3, message = "Password must have at least 3 characters")
    private String password = "";

    @JsonProperty("retype_password")
    private String retypePassword = "";

    @JsonProperty("date_of_birth")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DobConstrain(min = 18, message = "You must at least 18 years old!")
    //    @DateFormatConstrain(message = "Invalid date")
    @JsonInclude(JsonInclude.Include.NON_NULL)
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