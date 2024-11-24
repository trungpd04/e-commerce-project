package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.Shipment;
import com.nhom7.ecommercebackend.repository.ShipmentRepository;
import com.nhom7.ecommercebackend.request.shipment.ShipmentDTO;
import com.nhom7.ecommercebackend.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final ModelMapper modelMapper;

    @Override
    public Shipment findById(int id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new UnsupportedTemporalTypeException("Shipment method not supported"));
    }

    @Override
    public List<Shipment> findAll() {
        return shipmentRepository.findAll();
    }

    @Override
    public Shipment createShipment(ShipmentDTO shipment) {
        if (shipmentRepository.existsByType(String.valueOf(shipment.getType()))) {
            throw new DataIntegrityViolationException("Shipment type already exists");
        }else{
            Shipment newShipment = new Shipment();
            modelMapper.map(shipment, newShipment);
            return shipmentRepository.save(newShipment);
        }
    }

    @Override
    public Shipment updateShipment(int id, ShipmentDTO shipmentDTO) {
        if (!shipmentRepository.existsById(id)) {
            throw new DataNotFoundException("Shipment not exists");
        }
        Shipment  shipment =  shipmentRepository.findById(id)
                .orElseThrow(() -> new UnsupportedTemporalTypeException("Shipment method not supported"));
        shipment.setPrice(shipmentDTO.getPrice());
        shipment.setDescription(shipmentDTO.getDescription());
        shipment.setType(shipmentDTO.getType());
        shipment.setActive(shipmentDTO.isActive());
        shipment.setEstimatedDay(shipmentDTO.getEstimatedDay());
        return shipmentRepository.save(shipment);
    }

    @Override
    public void deleteById(int id) {
         Shipment  shipment =  shipmentRepository.findById(id)
                .orElseThrow(() -> new UnsupportedTemporalTypeException("Shipment method not supported"));
         shipment.setActive(false);
         shipmentRepository.save(shipment);
    }
}
