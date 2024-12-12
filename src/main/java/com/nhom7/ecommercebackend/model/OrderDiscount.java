package com.nhom7.ecommercebackend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class OrderDiscount {

    @JoinColumn(name = "order_id")
    @ManyToOne
    private Order order;

    @Id
    @ManyToOne
    @JoinColumn(name = "discount_id")
    private Discount discount;

    private BigDecimal discountAmount;
}
