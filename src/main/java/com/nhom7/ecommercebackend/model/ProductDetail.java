package com.nhom7.ecommercebackend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Table(name ="product_detail")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String cpu;

    private int ram;

    private String designDescription;
    private String osName;
    private String screen;
    private int batteryCapacity;
    private String manufacturer;
    private int guaranteeMonth;
    private String color;
    private int quantity;
}
