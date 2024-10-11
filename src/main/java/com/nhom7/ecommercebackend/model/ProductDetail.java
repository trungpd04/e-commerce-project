package com.nhom7.ecommercebackend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.model.Product;
import jakarta.persistence.*;
import lombok.*;


@Builder
@Table(name ="product_detail")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String cpu;

    private int ram;

    private String designDescription;
    private String osName;
    private String screenType;
    private int storage;
    private int screenRefreshRate;
    private float screenSize;
    private int batteryCapacity;
    private String manufacturer;
    private int guaranteeMonth;
    private String color;
    private int quantity;
}
