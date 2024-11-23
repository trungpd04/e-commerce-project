package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.controller.RatingController;
import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.*;
import com.nhom7.ecommercebackend.repository.*;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public Rating addRating(RatingDTO ratingDTO) {

        User user = userRepository.findById(ratingDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_EXIST.toString()));
        Product product = productRepository.findProductById(ratingDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Product does not exist!"));
        Optional<Rating> existRating = ratingRepository
                .findById(RatingId.builder()
                        .product(product)
                        .user(user)
                        .build());

        if (existRating.isPresent()) {
            throw new DataIntegrityViolationException("User has already reviewed this product!");
        }
        List<Order> orders = orderRepository
                .findAllByUserId(user.getId());
        OrderDetail orderDetail = null;
        for(Order order : orders) {
            Optional<OrderDetail> existingOrderDetails = orderDetailRepository
                    .findByOrderIdAndProductId(order.getId(), ratingDTO.getProductId());
            if(existingOrderDetails.isEmpty()) {
                continue;
            }else{
                orderDetail = existingOrderDetails.get();
            }
        }
        if(orderDetail == null) {
            throw new DataNotFoundException("User has not bought this product yet!");
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
        Product product = productRepository.findProductById(ratingDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Product does not exist!"));
        User user = userRepository.findById(ratingDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_EXIST.toString()));
        Rating existRating = ratingRepository
                .findById(
                        RatingId.builder()
                                .product(product)
                                .user(user)
                                .build()
                )
                .orElseThrow(() -> new DataNotFoundException("User have not rated this product yet!"));
        existRating.setRate(ratingDTO.getRate());
        existRating.setComment(ratingDTO.getComment());
        return ratingRepository.save(existRating);
    }

    @Override
    @Transactional
    public void deleteRating(Long userId, Long productId) {
        Product product = productRepository.findProductById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product does not exist!"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_EXIST.toString()));
        Rating existRating = ratingRepository
                .findById(RatingId.builder()
                        .product(product)
                        .user(user)
                        .build())
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

    @Override
    public float calculateAverageRating(Long productId) {
        List<Rating> ratings = ratingRepository.findAllByProductId(productId);
        float averageRating = 0;
        for(Rating rating : ratings) {
            averageRating += rating.getRate();
        }
        averageRating /= ratings.size();
        return (float) (Math.floor(averageRating * 10) / 10);
    }

    @Override
    public Page<Rating> getAllRating(PageRequest pageRequest) {
        return ratingRepository.findAll(pageRequest);
    }
}
