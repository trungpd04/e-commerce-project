package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
