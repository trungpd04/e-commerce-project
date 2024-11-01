package com.nhom7.ecommercebackend.response.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.model.CartItem;
import com.nhom7.ecommercebackend.response.product.ProductDetailResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("product")
    private ProductDetailResponse productDetailResponse;

    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    @JsonProperty("total_price")
    private BigDecimal totalPrice;
    public static CartItemResponse fromCartItem(CartItem cartItem) {
        return CartItemResponse.builder()
                .unitPrice(cartItem.getUnitPrice())
                .totalPrice(cartItem.getTotalPrice())
                .quantity(cartItem.getQuantity())
                .productDetailResponse(ProductDetailResponse.fromProduct(cartItem.getProduct()))
                .build();
    }
}
