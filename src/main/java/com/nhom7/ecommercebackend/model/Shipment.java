package com.nhom7.ecommercebackend.model;

import com.fasterxml.jackson.annotation.*;
import com.nhom7.ecommercebackend.validation.EnumConstraint;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.checkerframework.common.value.qual.EnumVal;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "shipments")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Shipment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "type")
//    @EnumConstraint(
//            value = {"free", "standard", "pro" },
//            message = "Invalid shipment method"
//    )
    private String type;

    private String description;

    @Column(columnDefinition = "DECIMAL(32, 0)")
    @Min(value = 0, message = "Price must be >= 0")
    private BigDecimal price;

    @Column(name = "estimated_day")
    @JsonProperty("estimated_day")
    private int estimatedDay;

    private boolean active;

    @OneToMany(mappedBy = "shipment")
    @JsonBackReference
    private List<Order> orders;

    @PrePersist
    private void setActive() {
        setActive(true);
    }
}
