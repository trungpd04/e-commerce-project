package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.ProductAttribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Integer> {
    Optional<ProductAttribute> findByName(String name);
    boolean existsByName(String name);
    Page<ProductAttribute> findAll(Pageable pageable);
}
