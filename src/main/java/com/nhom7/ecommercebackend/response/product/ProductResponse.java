package com.nhom7.ecommercebackend.response.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.SubCategory;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String thumbnail;
    private BigDecimal price;
    private boolean active;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("is_hot")
    private boolean isHot;

    private List<ProductAttributeValueResponse> attributes;
    @JsonProperty("category_name")
    private String category;
    private List<String> subcategory;

    public static ProductResponse fromProduct(Product product) {
        List<String> subcategory = new ArrayList<>();
        for (SubCategory subCategory : product.getSubcategory()) {
            subcategory.add(subCategory.getName());
        }
        List<ProductAttributeValueResponse> attributeValues = new ArrayList<>();
        product.getAttributeValues().forEach(attributeValue -> {
            if(attributeValue.getProductAttribute().getName().contains("ram")
                || attributeValue.getProductAttribute().getName().contains("screen_size")
                || attributeValue.getProductAttribute().getName().contains("storage")
            ) {
                ProductAttributeValueResponse response = ProductAttributeValueResponse.builder()
                        .name(attributeValue.getProductAttribute().getName())
                        .value(attributeValue.getValue())
                        .build();
                attributeValues.add(response);
            }
        });
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .thumbnail(product.getThumbnail())
                .active(product.getActive())
                .price(product.getPrice())
                .isHot(product.isHot())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .category(product.getSubcategory().getFirst().getCategory().getName())
                .attributes(attributeValues)
                .subcategory(subcategory)
                .build();
    }
}
