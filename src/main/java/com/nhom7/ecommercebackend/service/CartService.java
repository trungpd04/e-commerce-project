package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.model.Cart;
import com.nhom7.ecommercebackend.request.CartDTO;
import com.nhom7.ecommercebackend.request.CartItemDTO;
import io.swagger.v3.oas.models.OpenAPI;

import java.util.Optional;

public interface CartService {
    Cart getCardByUserId(Long userId);
    Cart createCart(CartDTO cartDTO);
    Cart addToCart(CartItemDTO cartItemDTO);
    Cart updateCart(CartDTO cartDTO);
    Cart deleteCartByUserId(Long userId);
}
