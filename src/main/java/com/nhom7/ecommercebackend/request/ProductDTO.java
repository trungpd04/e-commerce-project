package com.nhom7.ecommercebackend.request;

import lombok.Data;

@Data
public class ProductDTO {
    private String name;
    private String description;
    private float price;
    private String thumbnail;
    private Long categoryId; // Category ID for associating product with a category
}
