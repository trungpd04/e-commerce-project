package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.model.Rating;
import com.nhom7.ecommercebackend.request.rating.RatingDTO;
import com.nhom7.ecommercebackend.response.rating.RatingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface RatingService {
    Rating addRating(RatingDTO ratingDTO);
    Rating updateRating(RatingDTO ratingDTO);
    void deleteRating(Long userId, Long productId);
    Page<RatingResponse> getRatingByProductId(Long productId, PageRequest pageRequest);
    float calculateAverageRating(Long productId);
}
