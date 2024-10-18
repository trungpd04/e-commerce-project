package com.nhom7.ecommercebackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_attribute_values")
@Getter
@Setter
@Builder
public class ProductAttributeValue {

    @Id
    @Column(name = "value_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int valueId;

    @Column(name = "value")
    private String value;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "attribute_id", nullable = false)
    private ProductAttribute productAttribute;

    @Override
    public String toString() {
        return "ProductAttributeValue{" +
                "valueId=" + valueId +
                ", value='" + value + '\'' +
                '}';
    }
}
