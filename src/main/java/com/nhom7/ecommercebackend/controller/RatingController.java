package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.Rating;
import com.nhom7.ecommercebackend.request.rating.RatingDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.response.rating.RatingListResponse;
import com.nhom7.ecommercebackend.response.rating.RatingResponse;
import com.nhom7.ecommercebackend.service.RatingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/ratings")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse addRating(@RequestBody RatingDTO ratingDTO) {
        Rating newRating = ratingService.addRating(ratingDTO);
        return ApiResponse.builder()
                .message("Rate product successfully!")
                .status(HTTP_OK)
                .data(toRatingDTO(newRating))
                .build();
    }
    @GetMapping("/product/{id}")
    public ApiResponse getRatingByProduct(
            @PathVariable("id") Long productId,
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer pageNum,
            @RequestParam(value = "size", defaultValue = "3", required = false) Integer pageSize
    ) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        Page<RatingResponse> ratingResponsePage = ratingService
                .getRatingByProductId(productId, pageRequest);
        RatingListResponse ratingListResponse = RatingListResponse
                .builder()
                .pageNo(ratingResponsePage.getNumber())
                .pageSize(ratingResponsePage.getSize())
                .totalPages(ratingResponsePage.getTotalPages())
                .totalElements(ratingResponsePage.getTotalElements())
                .last(ratingResponsePage.isLast())
                .ratingResponses(ratingResponsePage.getContent())
                .build();
        return ApiResponse.builder()
                .message("Get product's rating successfully!")
                .status(HTTP_OK)
                .data(ratingListResponse)
                .build();
    }
    @GetMapping("/average_by_product/{productId}")
    public ApiResponse getAverageRatingByProduct(
            @PathVariable("productId") Long productId
    ) {
        float averageRating = ratingService.calculateAverageRating(productId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .data(averageRating)
                .build();
    }
    @PutMapping("")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse updateRating(
            @RequestBody RatingDTO ratingDTO
    ) {
        Rating rating = ratingService.updateRating(ratingDTO);
        return ApiResponse.builder()
                .message("Update rating successfully")
                .status(HTTP_OK)
                .data(toRatingDTO(rating))
                .build();
    }
    @DeleteMapping("/user/{userId}/product/{productId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse deleteRating(
            @PathVariable("userId") Long userId,
            @PathVariable("productId") Long productId
    ) {
        ratingService.deleteRating(userId, productId);
        return ApiResponse.builder()
                .message("Delete rating successfully")
                .status(HTTP_OK)
                .build();
    }
    private RatingDTO toRatingDTO(Rating rating) {
        return RatingDTO.builder()
                .userId(rating.getId().getUser().getId())
                .productId(rating.getId().getProduct().getId())
                .rate(rating.getRate())
                .comment(rating.getComment())
                .build();
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse getAllRating(
            @RequestParam(name = "size", defaultValue = "5", required = false) int size,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Rating> ratings = ratingService.getAllRating(pageRequest);
        return ApiResponse.builder()
                .message("Get all rating successfully")
                .data(ratings)
                .status(HTTP_OK)
                .build();
    }

}
