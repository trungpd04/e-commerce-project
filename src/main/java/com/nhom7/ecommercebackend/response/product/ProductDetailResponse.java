package com.nhom7.ecommercebackend.response.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.ProductImage;
import com.nhom7.ecommercebackend.model.SubCategory;
import com.nhom7.ecommercebackend.request.category.SubCategoryDTO;
import lombok.*;

import java.math.BigDecimal;
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
    private BigDecimal price;
    private boolean active;

//    @JsonProperty("category_name")
//    private Category category;

    private List<SubCategory> subcategory;

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

        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .thumbnail(product.getThumbnail())
                .active(product.getActive())
                .price(product.getPrice())
                .subcategory(product.getSubcategory())
                .attributes(attributeValues)
                .productImages(product.getProductImages())
                .build();
    }
}
