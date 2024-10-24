package com.nhom7.ecommercebackend.request.rating;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Builder
@Data
public class RatingDTO {
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("user_id")
    private Long userId;

    @Range(min = 1, max = 5, message = "Rate must be in range 1-5 star!")
    private int rate;
    private String comment;
}
