package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.OrderDetail;
import com.nhom7.ecommercebackend.model.Product;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    Optional<OrderDetail> findByOrderIdAndProductId(UUID orderId, Long product_id);
    OrderDetail findByProductId(Long product_id);

}
