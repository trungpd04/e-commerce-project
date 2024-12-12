package com.nhom7.ecommercebackend.request.discount;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.DateTime;

import java.math.BigDecimal;

public class DiscountDTO {

    private String code;
    private String description;

    @JsonProperty("discount_type")
    private int discountType;

    @JsonProperty("discount_value")
    private BigDecimal discountValue;

    @JsonProperty("min_order_value")
    private BigDecimal minOrderValue;

    @JsonProperty("max_discount")
    private BigDecimal maxDiscount;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private DateTime startDate;

    @JsonProperty("endDate")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private DateTime endDate;

    @JsonProperty("used_limit")
    private int usageLimit;

    @JsonProperty("used_count")
    private int usedCount;

    private boolean active;

    @JsonProperty("user_restriction")
    private Long userRestriction;
}
