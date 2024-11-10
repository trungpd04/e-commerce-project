package com.nhom7.ecommercebackend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactUs extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(namespace = "id")
    private Long id;

    @Column(name = "full_name")
    @JsonProperty(namespace = "full_name")
    private String fullName;

    @Column(name = "email_or_phone")
    @JsonProperty(namespace = "email_or_phone")
    private String emailOrPhoneNumber;

    @Column(name = "content")
    @JsonProperty(namespace = "content")
    private String content;
}
