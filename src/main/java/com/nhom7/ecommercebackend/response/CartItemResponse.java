package com.nhom7.ecommercebackend.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.model.CartItem;
import com.nhom7.ecommercebackend.request.ProductDTO;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CartItemResponse {

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("product")
    private ProductDetailResponse productDetailResponse;

    public static CartItemResponse fromCartItem(CartItem cartItem) {
        return CartItemResponse.builder()
                .quantity(cartItem.getQuantity())
                .productDetailResponse(ProductDetailResponse.fromProduct(cartItem.getProduct()))
                .build();
    }
}
