package com.nhom7.ecommercebackend.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.model.AuthProvider;
import com.nhom7.ecommercebackend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {

    private String email;
    private Long id;
    @JsonProperty("phone_number")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phoneNumber;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("profile_image")
    private String profileImage;
    @JsonProperty("birth")
    private Date dateOfBirth;
    @JsonProperty("address")
    private String address;
    @JsonProperty("no_password")
    private boolean noPassword;

    @JsonProperty("provider")
    private AuthProvider provider;

    @JsonProperty("provider_id")
    private String providerId;


    public static UserDetailResponse fromUser(User user) {
        return UserDetailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .address(user.getAddress())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .profileImage(user.getProfileImage())
                .noPassword(!StringUtils.hasText(user.getPassword()))
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .build();
    }
}
