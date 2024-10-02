package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.SubCategory;
import io.swagger.models.auth.In;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findProductById(Long id);
    boolean existsByName(String name);
    List<Product> findBySubcategory(SubCategory subCategory);
    @Query("SELECT p FROM Product p " +
            "JOIN p.subcategory sc " +
            "JOIN sc.category c " +
            "WHERE (:keyword IS NULL OR " +
            "TRIM(LOWER(p.name)) LIKE TRIM(LOWER(CONCAT('%', :keyword, '%'))) " +
            "OR TRIM(LOWER(c.name)) LIKE TRIM(LOWER(CONCAT('%', :keyword, '%'))) " +
            "OR TRIM(LOWER(sc.name)) LIKE TRIM(LOWER(CONCAT('%', :keyword, '%'))))" +
            "AND (:categoryId IS NULL OR c.id = :categoryId) " +
            "AND (:subcategoryId IS NULL OR sc.id = :subcategoryId) ")
    Page<Product> getAllProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            @Param("subcategoryId") Integer subcategoryId,
            Pageable pageable);

}
