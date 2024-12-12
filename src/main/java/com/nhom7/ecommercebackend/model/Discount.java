package com.nhom7.ecommercebackend.model;

import com.google.api.client.util.DateTime;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "discount")
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String description;

    @Enumerated(EnumType.ORDINAL)
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscount;
    private DateTime startDate;
    private DateTime endDate;
    private int usageLimit;
    private int usedCount;
    private boolean active;

    @Column(nullable = true)
    private Long userRestriction;
}
