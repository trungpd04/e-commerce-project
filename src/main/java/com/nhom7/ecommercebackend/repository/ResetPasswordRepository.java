package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.ResetPasswordToken;
import com.nhom7.ecommercebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetPasswordRepository extends JpaRepository<ResetPasswordToken, Long> {
    Optional<ResetPasswordToken> findByToken(String token);
    Optional<ResetPasswordToken> findByUser(User user);
    void deleteByUser(User user);
}
