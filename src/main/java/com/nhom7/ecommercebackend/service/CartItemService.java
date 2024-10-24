package com.nhom7.ecommercebackend.service;


import com.nhom7.ecommercebackend.model.CartItem;
import com.nhom7.ecommercebackend.request.cart.CartItemDTO;

public interface CartItemService {

    void deleteCartItemById(int cartItemId);
}
