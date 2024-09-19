package com.nhom7.ecommercebackend.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImageDTO {
    @JsonProperty("product_id")
    @NotBlank(message = "Product id can not be blank!")
    private Long productId;

    @JsonProperty("image_url")
    private String imageUrl;
}