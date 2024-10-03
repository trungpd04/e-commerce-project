package com.nhom7.ecommercebackend.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Filter {
    private String keyword;
    private Integer categoryId;
    private Integer subcategoryId;
    private String price;
    private String ram;
    private String screenType;
    private String screenSize;
    private String screenRefreshRate;
    private String storage;

}
