package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.Rating;
import com.nhom7.ecommercebackend.model.RatingId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, RatingId> {
    Page<Rating> getAllByProductId(Long productId, Pageable pageable);
    Optional<Rating> findByUserId(Long userId);
}
