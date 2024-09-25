package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.CartItem;
import com.nhom7.ecommercebackend.request.CartDTO;
import com.nhom7.ecommercebackend.request.CartItemDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.response.CartItemResponse;
import com.nhom7.ecommercebackend.service.CartItemService;
import com.nhom7.ecommercebackend.service.CartService;
import com.nhom7.ecommercebackend.service.impl.CartItemServiceImpl;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequestMapping("${api.prefix}/cart_items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping("")
    public ApiResponse createCartItem(@RequestBody CartItemDTO cartItemDTO) {
        CartItem cartItem = cartItemService.creatCartItem(cartItemDTO);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Create cart item successfully!")
                .data(toCartItemDTO(cartItem))
                .build();
    }
    @DeleteMapping("/{id}")
    public ApiResponse removeCartItem(@PathVariable("id") int cartItemId) {
        cartItemService.deleteCartItemById(cartItemId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Remove cart item successfully!")
                .build();
    }
    @PutMapping("/{id}")
    public ApiResponse updateCartItem(
            @PathVariable("id") int cartItemId,
            @RequestBody CartItemDTO cartItemDTO
            ) {
        CartItem cartItem = cartItemService.updateCartItemById(cartItemId, cartItemDTO);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Update cart item successfully!")
                .build();
    }
    private CartItemDTO toCartItemDTO(CartItem cartItem) {
        return CartItemDTO.builder()
                .cartId(cartItem.getCart().getId())
                .productId(cartItem.getProduct().getId())
                .quantity(cartItem.getQuantity())
                .build();
    }
}
