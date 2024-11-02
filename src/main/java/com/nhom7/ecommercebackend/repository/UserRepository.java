package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.AuthProvider;
import com.nhom7.ecommercebackend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phone);
    Page<User> findAllByFullNameContaining(String keyword, Pageable pageable);
    Optional<User> findByEmailAndPasswordNotNull(String email);
    Optional<User> findByPhoneNumberAndPasswordNotNull(String phoneNumber);
    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
}

