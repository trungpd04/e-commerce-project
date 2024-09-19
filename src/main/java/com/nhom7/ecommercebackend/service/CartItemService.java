package com.nhom7.ecommercebackend.service;


import com.nhom7.ecommercebackend.model.CartItem;
import com.nhom7.ecommercebackend.request.CartItemDTO;

public interface CartItemService {
    CartItem creatCartItem(CartItemDTO cartItemDTO);
    CartItem updateCartItem(CartItemDTO cartItemDTO);
    void deleteCartItem(int cartItemId);
}
