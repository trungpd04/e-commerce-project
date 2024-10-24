package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.Cart;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.response.cart.CartResponse;
import com.nhom7.ecommercebackend.service.CartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static java.net.HttpURLConnection.HTTP_OK;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/carts")
@SecurityRequirement(name = "bearer-key")
public class CartController {

    private final CartService cartService;

    @PutMapping("")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse updateCart(
            @RequestParam(name = "product_id") Long productId,
            @RequestParam(name = "quantity") int quantity
    ) {
        Cart existingCart = cartService.updateQuantity(productId, quantity);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Update cart's item quantity successfully!")
                .data(CartResponse.fromCart(existingCart))
                .build();
    }
    @GetMapping("/my-cart")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getCartByUserId() {
        Cart cart = cartService.getMyCart();
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Get cart by user id successfully!")
                .data(CartResponse.fromCart(cart))
                .build();
    }
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/items/{productId}")
    public ApiResponse addToCart(
            @PathVariable("productId") Long productId
    ) {
        Cart cart = cartService.addToCart(productId);
        return ApiResponse.builder()
                .message("Add item to card successfully!")
                .status(HTTP_OK)
                .data(CartResponse.fromCart(cart))
                .build();
    }
    @DeleteMapping("/items/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse removeItem(
            @PathVariable("productId") Long productId
    ) {
        Cart cart = cartService.removeItem(productId);
        if(cart == null) {
            return ApiResponse.builder()
                    .message("Empty Cart!")
                    .status(HTTP_OK)
                    .build();
        }else{
            return ApiResponse.builder()
                    .message("Remove cart's item successfully!")
                    .status(HTTP_OK)
                    .data(CartResponse.fromCart(cart))
                    .build();
        }
    }
}
