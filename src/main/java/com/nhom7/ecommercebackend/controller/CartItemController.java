package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.CartItem;
import com.nhom7.ecommercebackend.request.cart.CartItemDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.service.CartItemService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequestMapping("${api.prefix}/items")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class CartItemController {

    private final CartItemService cartItemService;

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse removeCartItem(@PathVariable("id") int cartItemId) {
        cartItemService.deleteCartItemById(cartItemId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Remove cart item successfully!")
                .build();
    }
}
