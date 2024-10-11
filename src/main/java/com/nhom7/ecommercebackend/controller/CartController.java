package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.Cart;
import com.nhom7.ecommercebackend.request.cart.CartDTO;
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

    @PostMapping("")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse createCart(@RequestBody CartDTO cartDTO) {
        Cart newCart = cartService.createCart(cartDTO);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Create cart successfully!")
                .data(toCartDTO(newCart))
                .build();
    }
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getCartByUserId(@PathVariable("userId") Long userId) {
        Cart cart = cartService.getCardByUserId(userId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Get cart by user id successfully!")
                .data(CartResponse.fromCart(cart))
                .build();
    }
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{cartId}/items/{productId}")
    public ApiResponse addToCart(
            @PathVariable("cartId") Long cartId,
            @PathVariable("productId") Long productId
    ) {
        Cart cart = cartService.addToCart(productId, cartId);
        return ApiResponse.builder()
                .message("Add item to card successfully!")
                .status(HTTP_OK)
                .data(CartResponse.fromCart(cart))
                .build();
    }
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse deleteCartByUserId(
            @PathVariable("userId") Long userId
    ) {
        cartService.deleteCartByUserId(userId);
        return ApiResponse.builder()
                .message("Delete User's cart successfully!")
                .status(HTTP_OK)
                .build();
    }
    @DeleteMapping("/{cartId}/items/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse removeItem(
            @PathVariable("cartId") Long cartId,
            @PathVariable("productId") Long productId
    ) {
        Cart cart = cartService.removeItem(productId, cartId);
        return ApiResponse.builder()
                .message("Add item to card successfully!")
                .status(HTTP_OK)
                .data(CartResponse.fromCart(cart))
                .build();
    }
    private CartDTO toCartDTO(Cart cart) {
        return CartDTO.builder()
                .userId(cart.getUser().getId())
                .numberOfProducts(cart.getNumberOfProducts())
                .build();
    }
}
