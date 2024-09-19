package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.exception.InvalidParamException;
import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.ProductImage;
import com.nhom7.ecommercebackend.model.SubCategory;
import com.nhom7.ecommercebackend.repository.CategoryRepository;
import com.nhom7.ecommercebackend.repository.ProductImageRepository;
import com.nhom7.ecommercebackend.repository.ProductRepository;
import com.nhom7.ecommercebackend.repository.SubCategoryRepository;
import com.nhom7.ecommercebackend.request.ProductDTO;
import com.nhom7.ecommercebackend.request.ProductImageDTO;
import com.nhom7.ecommercebackend.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        if (!productDTO.getName().isBlank() && productRepository.existsByName(productDTO.getName())) {
            throw new DataIntegrityViolationException("Product name already exists!");
        }
//        SubCategory subCategory = subCategoryRepository.findById(productDTO.getSubCategoryId())
//                .orElseThrow(() -> new DataNotFoundException("Subcategory does not exist!"));
        List<SubCategory> subCategories = new ArrayList<>();
//        subCategories.add(subCategory);
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .subcategory(subCategories)
                .build();

        return productRepository.save(newProduct);
    }
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    @Override
    public Product getProductById(Long productId) throws DataNotFoundException {
        return productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product not found for ID: " + productId));
    }
    @Override
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    @Override
    @Transactional
    public void deleteProduct(Long productId) throws DataNotFoundException {
        if (!productRepository.existsById(productId)) {
            throw new DataNotFoundException("Product not found for ID: " + productId);
        }
        productRepository.deleteById(productId);
    }

    @Override
    @Transactional
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws InvalidParamException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product does not exist!"));
        ProductImage productImage = ProductImage.builder()
                .imageUrl(productImageDTO.getImageUrl())
                .product(product)
                .build();
        int size = product.getProductImages().size();
        if(size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException("Number of images must be <= "
                    +ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        if(product.getThumbnail() == null) {
            product.setThumbnail(productImage.getImageUrl());
        }
        productRepository.save(product);
        return productImageRepository.save(productImage);
    }

}
