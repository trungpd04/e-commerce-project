package com.nhom7.ecommercebackend.response.shipment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.N;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ShipmentResponse {
    private int shipmentId;
}
