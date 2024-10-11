package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.model.Cart;
import com.nhom7.ecommercebackend.request.cart.CartDTO;

public interface CartService {
    Cart addToCart(Long productId, Long cartId);
    Cart getCardByUserId(Long userId);
    Cart createCart(CartDTO cartDTO);
    Cart removeItem(Long productId, Long cartId);
//    Cart addToCart(CartItemDTO cartItemDTO);
    Cart updateCart(CartDTO cartDTO);
    void deleteCartByUserId(Long userId);
}
