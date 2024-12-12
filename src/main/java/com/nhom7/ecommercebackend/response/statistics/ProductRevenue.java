package com.nhom7.ecommercebackend.response.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class ProductRevenue {

    @JsonProperty("product_id")
    private Long productId;

    private Long quantity;
    private BigDecimal revenue;
}
