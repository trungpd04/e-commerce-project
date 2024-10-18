package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.ProductAttribute;
import com.nhom7.ecommercebackend.model.ProductAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, Integer> {
    Optional<ProductAttributeValue> findByProductAndProductAttribute(Product product, ProductAttribute productAttribute);
    List<ProductAttributeValue> findAllByProductAttribute(ProductAttribute productAttribute);
}
