package com.nhom7.ecommercebackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "order_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@IdClass(OrderDetailId.class)
@Entity
public class OrderDetail {

    @Id
    @JoinColumn(name = "order_id")
    @ManyToOne
    private Order order;

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

}
