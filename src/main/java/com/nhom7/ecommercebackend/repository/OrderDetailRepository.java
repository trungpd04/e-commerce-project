package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    Optional<OrderDetail> findByOrderIdAndProductId(UUID orderId, Long product_id);
}
