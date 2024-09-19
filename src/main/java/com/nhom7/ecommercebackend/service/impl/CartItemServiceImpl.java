package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.Cart;
import com.nhom7.ecommercebackend.model.CartItem;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.ProductImage;
import com.nhom7.ecommercebackend.repository.CartItemRepository;
import com.nhom7.ecommercebackend.repository.CartRepository;
import com.nhom7.ecommercebackend.repository.ProductRepository;
import com.nhom7.ecommercebackend.request.CartItemDTO;
import com.nhom7.ecommercebackend.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CartItemServiceImpl implements CartItemService {
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    @Override
    public CartItem creatCartItem(CartItemDTO cartItemDTO) {
        Product existingProduct = productRepository.findProductById(cartItemDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Product does not exist!"));
        Cart cart = cartRepository.findById(cartItemDTO.getCartId())
                .orElseThrow(() -> new DataNotFoundException("Cart not found!"));
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .quantity(cartItemDTO.getQuantity())
                .product(existingProduct)
                .build();
        return cartItemRepository.save(cartItem);
    }

    @Override
    public CartItem updateCartItem(CartItemDTO cartItemDTO) {
        return null;
    }

    @Override
    public void deleteCartItem(int cartItemId) {

    }
}
