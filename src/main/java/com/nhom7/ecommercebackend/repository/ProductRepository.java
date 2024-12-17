package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.SubCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findProductById(Long id);
    boolean existsByName(String name);
    List<Product> findBySubcategory(SubCategory subCategory);
    Page<Product> findAllByActive(@Nullable Specification<Product> specification, Boolean active, Pageable pageable);
}
