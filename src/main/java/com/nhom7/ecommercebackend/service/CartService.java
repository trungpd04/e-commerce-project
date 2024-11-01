package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.model.Cart;

public interface CartService {
    Cart addToCart(Long productId);
    Cart updateQuantity(Long productId, int quantity);
    Cart getMyCart();
    Cart removeItem(Long productId);
    void deleteCartByUserId(Long userId);
}
