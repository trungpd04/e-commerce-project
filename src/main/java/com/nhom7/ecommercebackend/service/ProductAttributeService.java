package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.model.ProductAttribute;
import com.nhom7.ecommercebackend.request.product.AttributeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ProductAttributeService {
    ProductAttribute createProductAttribute(AttributeDTO attributeDTO);
    ProductAttribute getProductAttribute(int attributeId);
    Page<ProductAttribute> getAll(PageRequest pageRequest);
    ProductAttribute updateAttributeById(int attributeId, AttributeDTO attributeDTO);
    void deleteAttribute(int attributeId);
}
