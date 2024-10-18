package com.nhom7.ecommercebackend.response.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.ProductImage;
import com.nhom7.ecommercebackend.model.SubCategory;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductDetailResponse {
    private Long id;
    private String name;
    private String description;
    private String thumbnail;
    private float price;
    private boolean active;
    @JsonProperty("category_name")
    private String category;
    private List<String> subcategory;
    @JsonProperty("product_images")
    private List<ProductImage> productImages;
    private List<ProductAttributeValueResponse> attributes;
    public static ProductDetailResponse fromProduct(Product product) {
        List<ProductAttributeValueResponse> attributeValues = new ArrayList<>();
        product.getAttributeValues().forEach(attributeValue -> {
            ProductAttributeValueResponse response = ProductAttributeValueResponse.builder()
                    .name(attributeValue.getProductAttribute().getName())
                    .value(attributeValue.getValue())
                    .build();
            attributeValues.add(response);
        });
        List<String> subcategory = new ArrayList<>();
        for (SubCategory subCategory : product.getSubcategory()) {
            subcategory.add(subCategory.getName());
        }
        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .thumbnail(product.getThumbnail())
                .active(product.getActive())
                .subcategory(subcategory)
                .price(product.getPrice())
                .category(product.getSubcategory().get(0).getCategory().getName())
                .attributes(attributeValues)
                .productImages(product.getProductImages())
                .build();
    }
}
