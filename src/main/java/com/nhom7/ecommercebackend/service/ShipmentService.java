package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.model.Shipment;
import com.nhom7.ecommercebackend.request.shipment.ShipmentDTO;

import java.util.List;
import java.util.Optional;

public interface ShipmentService {
    Shipment findById(int id);
    List<Shipment> findAll();
    List<Shipment> findAllByAdmin();
    Shipment createShipment(ShipmentDTO shipment);
    Shipment updateShipment(int id, ShipmentDTO shipment);
    void deleteById(int id);
}
