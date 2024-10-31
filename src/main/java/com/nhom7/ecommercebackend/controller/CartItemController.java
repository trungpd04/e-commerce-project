package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.service.CartItemService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequestMapping("${api.prefix}/items")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class CartItemController {

    private final CartItemService cartItemService;

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse removeCartItem(@PathVariable("id") int cartItemId) {
        cartItemService.deleteCartItemById(cartItemId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Remove cart item successfully!")
                .build();
    }
}
