package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.exception.InvalidParamException;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.ProductImage;
import com.nhom7.ecommercebackend.request.ProductDTO;
import com.nhom7.ecommercebackend.request.ProductImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Product createProduct(ProductDTO productDTO) throws DataNotFoundException;
    List<Product> getAllProducts();
    Product getProductById(Long productId) throws DataNotFoundException;
    Product saveProduct(Product product);
    void deleteProduct(Long productId) throws DataNotFoundException;
    ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws InvalidParamException;
}
