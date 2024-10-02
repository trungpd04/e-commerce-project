package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.CartItem;
import com.nhom7.ecommercebackend.request.CartItemDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequestMapping("${api.prefix}/items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

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
