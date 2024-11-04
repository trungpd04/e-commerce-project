package com.nhom7.ecommercebackend.request.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.validation.DobConstrain;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateUserDTO {

    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber = "";

    @Pattern(regexp = "^(|.+@.+\\..+)$", message = "Invalid email format")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email = "";

    private String address = "";

    @JsonProperty("date_of_birth")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DobConstrain(min = 18, message = "You must at least 18 years old!")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date dateOfBirth;

    @JsonProperty("profile_image")
    private String profileImage;


    @NotNull(message = "Role ID is required")
    @JsonProperty("role_id")
    //role admin not permitted
    private Long roleId;
}
