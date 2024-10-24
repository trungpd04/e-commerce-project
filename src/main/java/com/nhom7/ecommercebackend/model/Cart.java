package com.nhom7.ecommercebackend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number_of_products")
    private int numberOfProducts;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @JsonProperty("total_money")
    private BigDecimal totalMoney;

    @OneToMany(mappedBy = "cart")
    private List<CartItem> cartItems;

    public void addItem(CartItem item) {
        this.cartItems.add(item);
        item.setCart(this);
        updateTotalAmountAndNumberOfProducts();
    }
    public void removeItem(CartItem item) {
        this.cartItems.remove(item);
        item.setCart(null);
        updateTotalAmountAndNumberOfProducts();
    }

    public void updateTotalAmountAndNumberOfProducts() {
        // cần sửa vì chưa đúng logic
        this.numberOfProducts = 0;
        for (CartItem item : cartItems) {
            this.numberOfProducts = this.numberOfProducts + item.getQuantity();
        }
        this.totalMoney = cartItems.stream().map(item -> {
            BigDecimal unitPrice = item.getUnitPrice();
            if (unitPrice == null) {
                return  BigDecimal.ZERO;
            }
            return unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
