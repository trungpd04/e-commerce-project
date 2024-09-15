package com.nhom7.ecommercebackend.model;

import jakarta.persistence.*;
import lombok.*;

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
    private int id;

    @Column(name = "total")
    private int total;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "cart")
    private List<CartItem> cartItems;
}
