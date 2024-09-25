package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.Cart;
import com.nhom7.ecommercebackend.model.CartItem;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.repository.CartItemRepository;
import com.nhom7.ecommercebackend.repository.CartRepository;
import com.nhom7.ecommercebackend.repository.ProductRepository;
import com.nhom7.ecommercebackend.request.CartItemDTO;
import com.nhom7.ecommercebackend.service.CartItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CartItemServiceImpl implements CartItemService {

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    @Override
    @Transactional
    public CartItem creatCartItem(CartItemDTO cartItemDTO) {
        Product existingProduct = productRepository.findProductById(cartItemDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Product does not exist!"));
        Cart cart = cartRepository.findById(cartItemDTO.getCartId())
                .orElseThrow(() -> new DataNotFoundException("Cart not found!"));
        CartItem existingCartItem = cartItemRepository.findByCartAndProduct(cart, existingProduct);
        CartItem newCartItem = null;
        if (existingCartItem != null) {
            // If the product already exists, update the quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemDTO.getQuantity());
            cartItemRepository.save(existingCartItem);
        } else {
            // If the product is not in the cart, add it as a new item
            newCartItem = CartItem.builder()
                    .cart(cart)
                    .quantity(cartItemDTO.getQuantity())
                    .product(existingProduct)
                    .build();
            cartItemRepository.save(newCartItem);
        }

        // Update cart's total quantity and product count
        updateCart(cart);

        return existingCartItem != null ? existingCartItem : newCartItem;
    }
    @Override
    @Transactional
    public CartItem updateCartItemById(int cartItemId, CartItemDTO cartItemDTO) {
        if (cartItemDTO.getQuantity() == 0) {
            deleteCartItemById(cartItemId);
            return null;
        } else {
            CartItem cartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(() -> new DataNotFoundException("Cart Item does not exist!"));
            cartItem.setQuantity(cartItemDTO.getQuantity());
            return cartItemRepository.save(cartItem);
        }
    }
    @Override
    @Transactional
    public void deleteCartItemById(int cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new DataNotFoundException("Cart Item does not exist!"));
        cartItemRepository.deleteById(cartItemId);
    }
    private void updateCart(Cart cart) {
        // Calculate total quantity and total products in the cart
        int totalQuantity = cart.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        float totalMoney = 0;
        for(CartItem cartItem : cart.getCartItems()) {
            totalMoney = totalQuantity + cartItem.getQuantity() * cartItem.getProduct().getPrice();
        }

        cart.setNumberOfProducts(totalQuantity);
        cart.setTotalMoney(totalMoney);

        cartRepository.save(cart);
    }
}
