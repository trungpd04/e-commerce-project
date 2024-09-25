package com.nhom7.ecommercebackend.service;


import com.nhom7.ecommercebackend.model.CartItem;
import com.nhom7.ecommercebackend.request.CartItemDTO;

public interface CartItemService {
    CartItem creatCartItem(CartItemDTO cartItemDTO);
    CartItem updateCartItemById(int cartItemId, CartItemDTO cartItemDTO);
    void deleteCartItemById(int cartItemId);
}
