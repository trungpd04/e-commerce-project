package com.nhom7.ecommercebackend.request;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
public class ProductDTO {
    @Range(min = 3, message = "Product name must have at least 3 characters")
    private String name;
    @Range(min = 3, message = "Product description must have at least 3 characters")
    private String description;
    private float price;
    private String thumbnail;
    private int subCategoryId;
    private int categoryId;
}
