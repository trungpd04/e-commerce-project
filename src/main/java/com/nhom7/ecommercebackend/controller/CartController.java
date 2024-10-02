package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.Cart;
import com.nhom7.ecommercebackend.model.CartItem;
import com.nhom7.ecommercebackend.repository.CartRepository;
import com.nhom7.ecommercebackend.request.CartDTO;
import com.nhom7.ecommercebackend.request.CartItemDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.response.CartResponse;
import com.nhom7.ecommercebackend.service.CartService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static java.net.HttpURLConnection.HTTP_OK;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping("")
    public ApiResponse createCart(@RequestBody CartDTO cartDTO) {
        Cart newCart = cartService.createCart(cartDTO);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Create cart successfully!")
                .data(toCartDTO(newCart))
                .build();
    }
    @GetMapping("/{userId}")
    public ApiResponse getCartByUserId(@PathVariable("userId") Long userId) {
        Cart cart = cartService.getCardByUserId(userId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Get cart by user id successfully!")
                .data(CartResponse.fromCart(cart))
                .build();
    }
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
