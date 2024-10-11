package com.nhom7.ecommercebackend.response.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.model.Cart;
import com.nhom7.ecommercebackend.model.CartItem;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
@Builder
public class CartResponse {

    @JsonProperty("cart_id")
    private Long cartId;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("total_money")
    private Float totalMoney;
    @JsonProperty("number_of_products")
    private int numberOfProducts;
    @JsonProperty("cart_items")
    private List<CartItemResponse> cartItemResponses;

    public static CartResponse fromCart(Cart cart) {
        List<CartItemResponse> cartItemResponseList = new ArrayList<>();
        for(CartItem cartItem : cart.getCartItems()) {
            CartItemResponse cartItemResponse = CartItemResponse.fromCartItem(cartItem);
            cartItemResponseList.add(cartItemResponse);
        }
        float totalMoney = 0;
        int numberOfProducts = 0;
        for(CartItem cartItem : cart.getCartItems()) {
            numberOfProducts += cartItem.getQuantity();
            totalMoney += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }
        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .numberOfProducts(numberOfProducts)
                .totalMoney(totalMoney)
                .cartItemResponses(cartItemResponseList)
                .build();
    }
}
