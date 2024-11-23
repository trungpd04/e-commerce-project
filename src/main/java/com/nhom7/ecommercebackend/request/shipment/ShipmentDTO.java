package com.nhom7.ecommercebackend.request.shipment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ShipmentDTO {

    private String type;
    private String description;

    @JsonProperty(namespace = "estimated_date")
    private int estimatedDate;
    private BigDecimal price;
    private boolean active;

}
