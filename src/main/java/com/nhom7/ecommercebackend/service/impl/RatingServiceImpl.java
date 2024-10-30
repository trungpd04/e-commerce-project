package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.Rating;
import com.nhom7.ecommercebackend.model.RatingId;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.repository.ProductRepository;
import com.nhom7.ecommercebackend.repository.RatingRepository;
import com.nhom7.ecommercebackend.repository.UserRepository;
import com.nhom7.ecommercebackend.request.rating.RatingDTO;
import com.nhom7.ecommercebackend.response.rating.RatingResponse;
import com.nhom7.ecommercebackend.service.RatingService;
import com.nhom7.ecommercebackend.exception.MessageKeys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;

    @Override
    @Transactional
    public Rating addRating(RatingDTO ratingDTO) {

        User user = userRepository.findById(ratingDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_EXIST.toString()));
        Product product = productRepository.findProductById(ratingDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Product does not exist!"));
        Optional<Rating> existRating = ratingRepository.findByUserId(ratingDTO.getUserId());

        if (existRating.isPresent()) {
            throw new DataIntegrityViolationException("User has already reviewed this product!");
        }

        Rating newRating = Rating.builder()
                .rate(ratingDTO.getRate())
                .id(RatingId.builder().user(user).product(product).build())
                .comment(ratingDTO.getComment())
                .build();
        return ratingRepository.save(newRating);
    }

    @Override
    @Transactional
    public Rating updateRating(RatingDTO ratingDTO) {
        Rating existRating = ratingRepository.findByUserId(ratingDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User have not rated this product yet!"));
        existRating.setRate(ratingDTO.getRate());
        existRating.setComment(ratingDTO.getComment());
        return ratingRepository.save(existRating);
    }

    @Override
    @Transactional
    public void deleteRating(Long userId, Long productId) {

        Rating existRating = ratingRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("User have not rated any product yet!"));

        if(!Objects.equals(existRating.getId().getProduct().getId(), productId)) {
            throw new DataNotFoundException("User have not rated this product yet!");
        }else{
            ratingRepository.delete(existRating);
        }

    }

    @Override
    public Page<RatingResponse> getRatingByProductId(Long productId, PageRequest pageRequest) {
        Page<Rating> page = ratingRepository.getAllByProductId(productId, pageRequest);
        return page.map(RatingResponse::fromRating);
    }

}
