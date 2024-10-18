package com.nhom7.ecommercebackend.response.product;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductAttributeValueResponse {
    private String name;
    private String value;
}
