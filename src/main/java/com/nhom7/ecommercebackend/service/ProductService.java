package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.exception.InvalidParamException;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.ProductImage;
import com.nhom7.ecommercebackend.repository.Filter;
import com.nhom7.ecommercebackend.request.ProductDTO;
import com.nhom7.ecommercebackend.request.ProductImageDTO;
import com.nhom7.ecommercebackend.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Product createProduct(ProductDTO productDTO) throws DataNotFoundException;
    Product getProductById(Long productId) throws DataNotFoundException;
    void deleteProduct(Long productId) throws DataNotFoundException;
    Product updateProduct(Long productId, ProductDTO productDTO);
    ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws InvalidParamException;
    Page<ProductResponse> getAllProduct(String keyword, Integer categoryId, Integer subcategoryId,  PageRequest pageRequest);
    Page<ProductResponse> getAllProductFilter(Filter filter, PageRequest pageRequest);
}
