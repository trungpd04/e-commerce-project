package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.exception.MessageKeys;
import com.nhom7.ecommercebackend.model.Cart;
import com.nhom7.ecommercebackend.model.CartItem;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.repository.CartItemRepository;
import com.nhom7.ecommercebackend.repository.CartRepository;
import com.nhom7.ecommercebackend.repository.ProductRepository;
import com.nhom7.ecommercebackend.service.CartService;
import com.nhom7.ecommercebackend.utils.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserUtil userUtil;

    @Override
    @Transactional
    public Cart addToCart(Long productId) {
        Product existingProduct = productRepository.findProductById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product does not exist!"));
        User loggedInUser = userUtil.getLoggedInUser();
        Optional<Cart> optionalCart = cartRepository.findByUser(loggedInUser);
        Cart cart = optionalCart.orElseGet(() -> cartRepository.save(Cart.builder()
                .cartItems(new ArrayList<>())
                .numberOfProducts(0)
                .totalMoney(BigDecimal.ZERO)
                .user(loggedInUser)
                .build()));
        CartItem cartItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElse(new CartItem());
        if (cartItem.getId() == null ){
            cartItem.setCart(cart);
            cartItem.setProduct(existingProduct);
            cartItem.setQuantity(1);
            cartItem.setUnitPrice(existingProduct.getPrice());
            cart.addItem(cartItem);
        }
        else {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cart.updateTotalAmountAndNumberOfProducts();
        }
        cartItem.setTotalPrice();
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
        return cart;
    }

    @Override
    public Cart updateQuantity(Long productId, int quantity) {
        Product existingProduct = productRepository
                .findProductById(productId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.PRODUCT_NOT_FOUND.toString()));
        CartItem existingCartItem = cartItemRepository
                .findByProduct(existingProduct)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.CART_ITEM_NOT_FOUND.toString()));
        existingCartItem.setQuantity(quantity);
        existingCartItem.setTotalPrice();
        cartItemRepository.save(existingCartItem);
        Cart cart = existingCartItem.getCart();
        cart.updateTotalAmountAndNumberOfProducts();
        cartRepository.save(cart);
        return cart;
    }

    @Override
    public Cart getMyCart() {
        User user = userUtil.getLoggedInUser();
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.CART_EMPTY.toString()));
    }
    @Override
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public Cart removeItem(Long productId) {
        Cart existingCart = getMyCart();
        Product existingProduct = productRepository.findProductById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product does not exist!"));
        CartItem cartItem = cartItemRepository.findByProduct(existingProduct)
                        .orElseThrow(() -> new DataNotFoundException(MessageKeys.CART_ITEM_NOT_FOUND.toString()));
        existingCart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);
        if(existingCart.getCartItems().isEmpty()) {
            existingCart.setUser(null);
            cartRepository.delete(existingCart);
            return null;
        }else {
            return cartRepository.save(existingCart);
        }
    }
    @Override
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public void deleteCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                        .orElseThrow(() -> new DataNotFoundException(("Cart does not exist!")));
        cartRepository.deleteByUserId(userId);
    }
}
