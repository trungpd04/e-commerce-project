package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.CartItem;
import com.nhom7.ecommercebackend.request.CartDTO;
import com.nhom7.ecommercebackend.request.CartItemDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.response.CartItemResponse;
import com.nhom7.ecommercebackend.service.CartItemService;
import com.nhom7.ecommercebackend.service.CartService;
import com.nhom7.ecommercebackend.service.impl.CartItemServiceImpl;
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
//    @GetMapping("/{id}")
//    public CartItemResponse cartItemResponse(@PathVariable("id") int id) {
//
//    }
    private CartItemDTO toCartItemDTO(CartItem cartItem) {
        return CartItemDTO.builder()
                .cartId(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .quantity(cartItem.getQuantity())
                .build();
    }
}
