package com.nhom7.ecommercebackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_attributes")
@Getter
@Setter
@Builder
public class ProductAttribute  {

    @Id
    @Column(name = "attribute_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int attributeId;

    @Column(name = "name", length = 64, nullable = false)
    private String name;

}
