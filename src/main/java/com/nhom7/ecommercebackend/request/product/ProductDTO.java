package com.nhom7.ecommercebackend.request.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.request.product.ProductDetailDTO;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Data
@Builder
public class ProductDTO {

    @Range(min = 3, message = "Product name must have at least 3 characters")
    private String name;

    @Range(min = 3, message = "Product description must have at least 3 characters")
    private String description;

    private float price;

    private String thumbnail;

    @JsonProperty("category_id")
    private Long categoryId;

    private List<Long> subcategory;

    @JsonProperty("product_detail")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ProductDetailDTO productDetailDTO;
}

