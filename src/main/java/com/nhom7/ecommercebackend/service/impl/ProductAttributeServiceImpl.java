package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.ProductAttribute;
import com.nhom7.ecommercebackend.model.ProductAttributeValue;
import com.nhom7.ecommercebackend.repository.ProductAttributeRepository;
import com.nhom7.ecommercebackend.repository.ProductAttributeValueRepository;
import com.nhom7.ecommercebackend.request.product.AttributeDTO;
import com.nhom7.ecommercebackend.service.ProductAttributeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAttributeServiceImpl implements ProductAttributeService {

    private final ProductAttributeRepository productAttributeRepository;
    private final ProductAttributeValueRepository productAttributeValueRepository;

    @Override
    @Transactional
    public ProductAttribute createProductAttribute(AttributeDTO attributeDTO) {
        if(productAttributeRepository.existsByName(attributeDTO.getName())) {
            throw new DataIntegrityViolationException("Attribute name has already exist!");
        }
        return productAttributeRepository.save(ProductAttribute.builder()
                .name(attributeDTO.getName())
                .active(attributeDTO.isActive())
                .build());
    }

    @Override
    public ProductAttribute getProductAttribute(int attributeId) {
        return productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new DataNotFoundException("Product Attribute not exist!"));
    }

    @Override
    public Page<ProductAttribute> getAll(PageRequest pageRequest) {
        return productAttributeRepository.findAll(pageRequest);
    }

    @Override
    @Transactional
    public ProductAttribute updateAttributeById(int attributeId, AttributeDTO attributeDTO) {
        ProductAttribute productAttribute = productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new DataNotFoundException("Product Attribute not exist!"));
        productAttribute.setName(attributeDTO.getName());
        productAttribute.setActive(attributeDTO.isActive());
        return productAttributeRepository.save(productAttribute);
    }

    @Override
    @Transactional
    public void deleteAttribute(int attributeId) {
        ProductAttribute productAttribute = productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new DataNotFoundException("Product Attribute not exist!"));
//        List<ProductAttributeValue> productAttributeValue = productAttributeValueRepository
//                .findAllByProductAttribute(productAttribute);
//        if(!productAttributeValue.isEmpty()) {
//            throw new DataIntegrityViolationException("Product Attribute has already associated with Value!");
//        }else{
//            productAttributeRepository.delete(productAttribute);
//        }
        productAttribute.setActive(false);
        productAttributeRepository.save(productAttribute);
    }
}
