package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.Cart;
import com.nhom7.ecommercebackend.model.CartItem;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.repository.CartItemRepository;
import com.nhom7.ecommercebackend.repository.CartRepository;
import com.nhom7.ecommercebackend.repository.ProductRepository;
import com.nhom7.ecommercebackend.repository.UserRepository;
import com.nhom7.ecommercebackend.request.CartDTO;
import com.nhom7.ecommercebackend.request.CartItemDTO;
import com.nhom7.ecommercebackend.request.ProductDTO;
import com.nhom7.ecommercebackend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public Cart getCardByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("User does not have a cart!"));
        return cart;
    }

    @Override
    public Cart createCart(CartDTO cartDTO) {
        User existingUser = userRepository.findById(cartDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User does not exist!"));
        List<CartItem> cartItems = new ArrayList<>();
        Cart cart = Cart.builder()
                .cartItems(cartItems)
                .user(existingUser)
                .numberOfProducts(0)
                .totalMoney(0f)
                .build();
        return cartRepository.save(cart);
    }

    @Override
    public Cart updateCart(CartDTO cartDTO) {
        return null;
    }

    @Override
    public Cart deleteCartByUserId(Long userId) {
        return null;
    }
}
