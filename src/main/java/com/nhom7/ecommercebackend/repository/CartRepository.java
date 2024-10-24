package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.Cart;
import com.nhom7.ecommercebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
    Optional<Cart> findByUser(User user);
    void deleteByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
