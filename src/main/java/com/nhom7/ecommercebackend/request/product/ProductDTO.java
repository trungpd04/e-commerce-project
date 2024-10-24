package com.nhom7.ecommercebackend.request.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductDTO {

    @Range(min = 3, message = "Product name must have at least 3 characters")
    private String name;

    @Range(min = 3, message = "Product description must have at least 3 characters")
    private String description;

    private BigDecimal price;

    private String thumbnail;

    @JsonProperty("category_id")
    private Long categoryId;

    private List<Long> subcategory;
    private Long quantity;
    @JsonProperty("product_attributes")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ProductAttributeValueDTO> attributeValues;
}
