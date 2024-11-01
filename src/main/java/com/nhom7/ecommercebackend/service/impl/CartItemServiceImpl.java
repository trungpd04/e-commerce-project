package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.CartItem;
import com.nhom7.ecommercebackend.repository.CartItemRepository;
import com.nhom7.ecommercebackend.service.CartItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public void deleteCartItemById(int cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new DataNotFoundException("Cart Item does not exist!"));
        cartItemRepository.deleteById(cartItemId);
    }
}
