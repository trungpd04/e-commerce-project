package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findProductById(Long id);
    boolean existsByName(String name);
    List<Product> findBySubcategory(SubCategory subCategory);
}
