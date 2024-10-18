package com.nhom7.ecommercebackend.response.product;

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
                .category(product.getSubcategory().getFirst().getCategory().getName())
                .attributes(attributeValues)
                .subcategory(subcategory)
                .build();
    }
}
