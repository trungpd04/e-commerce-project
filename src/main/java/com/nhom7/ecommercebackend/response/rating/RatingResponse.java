package com.nhom7.ecommercebackend.response.rating;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.model.Rating;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class RatingResponse {
    @JsonProperty("user_full_name")
    private String userFullName;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("rate")
    private int rate;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updateAt;

    public static RatingResponse fromRating(Rating rating) {
        return RatingResponse.builder()
                .userFullName(rating.getId().getUser().getFullName())
                .productId(rating.getId().getProduct().getId())
                .rate(rating.getRate())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .updateAt(rating.getUpdatedAt())
                .build();
    }
}
