package com.nhom7.ecommercebackend.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.model.CartItem;
import com.nhom7.ecommercebackend.request.ProductDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {
    @JsonProperty("cart_id")
    private int cartId;
    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("product_list")
    private ProductDTO productDTO;

    public static CartItemResponse fromCartItem(CartItem cartItem) {

        ProductDTO productDTO1 = ProductDTO.builder()
                .name(cartItem.getProduct().getName())
                .description(cartItem.getProduct().getDescription())
                .price(cartItem.getProduct().getPrice())
                .thumbnail(cartItem.getProduct().getThumbnail())
                .subCategoryId(cartItem.getProduct().getSubcategory().getFirst().getId())
                .categoryId(cartItem.getProduct().getSubcategory().getFirst().getCategory().getId())
                .build();
        return CartItemResponse.builder()
                .cartId(cartItem.getCart().getId())
                .quantity(cartItem.getQuantity())
                .productDTO(productDTO1)
                .build();
    }

}
