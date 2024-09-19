package com.nhom7.ecommercebackend.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemDTO {

    @JsonProperty("cart_id")
    private int cartId;

    private int quantity;


    @JsonProperty("product_id")
    private Long productId;

}
