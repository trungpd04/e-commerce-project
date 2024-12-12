package com.nhom7.ecommercebackend.response.statistics;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenue {
    private int month;
    private int year;
    private BigDecimal revenue;
}
