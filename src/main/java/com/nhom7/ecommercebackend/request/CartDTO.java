package com.nhom7.ecommercebackend.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartDTO {

    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("number_of_products")
    private int numberOfProducts;

}
