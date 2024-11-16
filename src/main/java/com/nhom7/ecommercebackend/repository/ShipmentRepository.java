package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {
    boolean existsByType(String type);
}
