package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.Rating;
import com.nhom7.ecommercebackend.model.RatingId;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, RatingId> {

    @Query("SELECT r from Rating  r where r.id.product.id = :productId")
    Page<Rating> getAllByProductId(@Param("productId") Long productId, Pageable pageable);


    @NonNull
    Optional<Rating> findById(@NonNull RatingId id);

    @Query("SELECT r from Rating r where r.id.product.id = :productId")
    List<Rating> findAllByProductId(@Param("productId") Long productId);

    @Query("select r from Rating r " +
            "WHERE (:keyword IS NULL OR REPLACE(LOWER(r.id.user.fullName), ' ', '') LIKE REPLACE(LOWER(CONCAT('%', :keyword, '%')),' ', '' )) " +
            "OR REPLACE(LOWER(r.id.product.name), ' ', '') LIKE REPLACE(LOWER(CONCAT('%', :keyword, '%')),' ', '' )")
    Page<Rating> findAll(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
