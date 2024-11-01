package com.nhom7.ecommercebackend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;


@Builder
@Entity
@Table(name = "cart_items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;
    @ManyToOne
    @JoinColumn(name = "card_id")
    private Cart cart;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;


    public void setTotalPrice() {
        this.totalPrice = this.unitPrice.multiply(new BigDecimal(quantity));

    }
}
