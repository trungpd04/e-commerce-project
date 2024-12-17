package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.exception.InvalidParamException;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.ProductImage;
import com.nhom7.ecommercebackend.repository.filter.Filter;
import com.nhom7.ecommercebackend.request.product.ProductDTO;
import com.nhom7.ecommercebackend.request.product.ProductImageDTO;
import com.nhom7.ecommercebackend.response.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ProductService {
    Product createProduct(ProductDTO productDTO) throws DataNotFoundException;
    Product getProductById(Long productId) throws DataNotFoundException;
    void deleteProduct(Long productId) throws DataNotFoundException;
    Product updateProduct(Long productId, ProductDTO productDTO);
    ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws InvalidParamException;
    Page<ProductResponse> getAllProductFilter(Filter filter, PageRequest pageRequest);
    Page<ProductResponse> getAllActiveProductFilter(Filter filter, PageRequest pageRequest);
}
