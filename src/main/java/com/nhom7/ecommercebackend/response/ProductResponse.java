package com.nhom7.ecommercebackend.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.SubCategory;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String thumbnail;
    private float price;
    private boolean active;
    @JsonProperty("category_name")
    private String category;
    private List<String> subcategory;

    public static ProductResponse fromProduct(Product product) {
        List<String> subcategory = new ArrayList<>();
        for (SubCategory subCategory : product.getSubcategory()) {
            subcategory.add(subCategory.getName());
        }
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .thumbnail(product.getThumbnail())
                .price(product.getPrice())
                .active(product.getActive())
                .category(product.getSubcategory().getFirst().getCategory().getName())
                .subcategory(subcategory)
                .build();
    }
}
