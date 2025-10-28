package com.nhom7.ecommercebackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "reset_password_tokens")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordToken {
    public static final int EXPIRY_TIME = 3600;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Date expiryDate;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}
