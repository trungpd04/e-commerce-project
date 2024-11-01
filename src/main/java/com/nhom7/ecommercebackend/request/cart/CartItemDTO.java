package com.nhom7.ecommercebackend.request.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CartItemDTO {

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("quantity")
    private int quantity;

}
