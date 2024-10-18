package com.nhom7.ecommercebackend.request.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductAttributeValueDTO {
    @JsonProperty("name")
    private String attributeName;
    private String value;
}
