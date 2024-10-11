package com.nhom7.ecommercebackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class InvalidatedToken {
    @Id
    private String id;

    @Column(name = "expiry_time")
    private Date expiryTime;
}
