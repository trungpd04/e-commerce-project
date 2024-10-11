package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByUserId(Long userId);
    @Query("SELECT o FROM Order o where TRIM(o.user.fullName) LIKE TRIM(LOWER(CONCAT('%', :keyword, '%')))")
    Page<Order> findAll(@Param("keyword")String keyword, Pageable pageable);

    Page<Order> findAllByUserId(Long userId, Pageable pageable);
}
