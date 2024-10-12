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
import com.nhom7.ecommercebackend.request.cart.CartDTO;
import com.nhom7.ecommercebackend.service.CartService;
import com.nhom7.ecommercebackend.exception.MessageKeys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Transactional
    public Cart addToCart(Long productId, Long cartId) {
        Product existingProduct = productRepository.findProductById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product does not exist!"));
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new DataNotFoundException("Cart not found!"));
        CartItem existingCartItem = cartItemRepository.findByCartAndProduct(cart, existingProduct);
        CartItem newCartItem = null;
        if (existingCartItem != null) {
            // If the product already exists, update the quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + 1);
            cartItemRepository.save(existingCartItem);
        } else {
            // If the product is not in the cart, add it as a new item
            newCartItem = CartItem.builder()
                    .cart(cart)
                    .quantity(1)
                    .product(existingProduct)
                    .build();
            cartItemRepository.save(newCartItem);
        }

        // Update cart's total quantity and product count
        updateCart(cart);

        return cart;
    }

    @Override
    public Cart getCardByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("User does not have a cart!"));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public Cart createCart(CartDTO cartDTO) {
        User existingUser = userRepository.findById(cartDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_EXIST.toString()));
        if(cartRepository.existsByUserId(cartDTO.getUserId())) {
            throw new DataIntegrityViolationException("User's cart has already existed!");
        }
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
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public Cart removeItem(Long productId, Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new DataNotFoundException("Cart not found!"));
        Product existingProduct = productRepository.findProductById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product does not exist!"));
        CartItem cartItem = cartItemRepository.findByProduct(existingProduct);
        if(cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() - 1);
            if(cartItem.getQuantity() == 0) {
                cartItemRepository.deleteById(cartItem.getId());
            }else{
                cartItemRepository.save(cartItem);
            }
        }else{
            throw new DataNotFoundException("Cart Item does not exist!");
        }
        return cart;
    }

    @Override
    public Cart updateCart(CartDTO cartDTO) {
        return null;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public void deleteCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                        .orElseThrow(() -> new DataNotFoundException(("Cart does not exist!")));
        cartRepository.deleteByUserId(userId);
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
