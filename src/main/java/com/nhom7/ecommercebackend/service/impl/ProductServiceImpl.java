package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.exception.InvalidParamException;
import com.nhom7.ecommercebackend.model.*;
import com.nhom7.ecommercebackend.repository.*;
import com.nhom7.ecommercebackend.repository.filter.Filter;
import com.nhom7.ecommercebackend.repository.filter.FilterSpecification;
import com.nhom7.ecommercebackend.request.product.ProductAttributeValueDTO;
import com.nhom7.ecommercebackend.request.product.ProductDTO;
import com.nhom7.ecommercebackend.request.product.ProductImageDTO;
import com.nhom7.ecommercebackend.response.product.ProductResponse;
import com.nhom7.ecommercebackend.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductAttributeValueRepository productAttributeValueRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    @CacheEvict("products")
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {

        if (!productDTO.getName().isBlank() && productRepository.existsByName(productDTO.getName())) {
            throw new DataIntegrityViolationException("Product name already exists!");
        }
        Product newProduct = buildProduct(productDTO);

        return productRepository.save(newProduct);
    }

    @Cacheable("products")
    @Override
    public Product getProductById(Long productId) throws DataNotFoundException {
        return productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product not found for ID: " + productId));
    }

    @Override
    @Transactional
    @CacheEvict("products")
    public void deleteProduct(Long productId) throws DataNotFoundException {
        Product product = productRepository.findProductById(productId)
                        .orElseThrow(() -> new DataNotFoundException("Product not found for ID: " + productId));
        List<OrderDetail> orderDetail = orderDetailRepository.findAllByProductId(productId);
        if(!orderDetail.isEmpty()){
            for (OrderDetail orderDetail1 : orderDetail) {
                if (orderDetail1.getOrder().getStatus().equalsIgnoreCase("pending")
                        || orderDetail1.getOrder().getStatus().equalsIgnoreCase("processing")) {
                    throw new DataIntegrityViolationException("Can not delete the processing product! Please process the order ");
                }
            }
        }else{
            product.setActive(false);
        }
        productRepository.save(product);
    }

    @Override
    @Transactional
    @CacheEvict("products")
    public Product updateProduct(Long productId, ProductDTO productDTO) {
        Product existingProduct = productRepository.findProductById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product does not exist!"));

        existingProduct.setName(productDTO.getName());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setThumbnail(productDTO.getThumbnail());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setQuantity(productDTO.getQuantity());
        existingProduct.setActive(productDTO.isActive());
        existingProduct.setHot(productDTO.isHot());
        if (productDTO.getAttributeValues() != null) {
            for (ProductAttributeValueDTO productAttributeValueDTO : productDTO.getAttributeValues()) {
                String attributeName = productAttributeValueDTO.getAttributeName();
                String attributeValue = productAttributeValueDTO.getValue();

                ProductAttribute productAttribute = productAttributeRepository.findByName(attributeName)
                        .orElseThrow(() -> new DataNotFoundException("Attribute not found with name: " + attributeName));

                ProductAttributeValue productAttributeValue = productAttributeValueRepository
                        .findByProductAndProductAttribute(existingProduct, productAttribute)
                        .orElse(new ProductAttributeValue());

                productAttributeValue.setProduct(existingProduct);
                productAttributeValue.setProductAttribute(productAttribute);
                productAttributeValue.setValue(attributeValue);

                productAttributeValueRepository.save(productAttributeValue);
            }
        }
        return productRepository.save(existingProduct);
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
            throw new InvalidParamException("Number of images must be <= " + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        if(product.getThumbnail() == null) {
            product.setThumbnail(productImage.getImageUrl());
        }
        productRepository.save(product);
        return productImageRepository.save(productImage);
    }

    @Override
    @Cacheable("products")
    public Page<ProductResponse> getAllProductFilter(Filter filter, PageRequest pageRequest) {
        Page<Product> productPage;
        Specification<Product> specification = new FilterSpecification<>(filter);
        productPage = productRepository.findAll(specification, pageRequest);
        return productPage.map(ProductResponse::fromProduct);
    }

    @Override
    @Cacheable("products")
    public Page<ProductResponse> getAllActiveProductFilter(Filter filter, PageRequest pageRequest) {
        Page<Product> productPage;
        Specification<Product> specification = new FilterSpecification<>(filter);

        specification = specification.and((root, query, criteriaBuilder) -> {
               return criteriaBuilder.equal(root.get("active"), true);
    }
        );
//                .and((root, query, criteriaBuilder) -> {
//            Join<Product, SubCategory> productSubCategoryJoin = null;
//            Join<Product, Category> productCategoryJoin = null;
//            productSubCategoryJoin = root.join("subcategory", JoinType.LEFT);
//            productCategoryJoin = productSubCategoryJoin.join("category", JoinType.LEFT);
//            return criteriaBuilder.equal(productCategoryJoin.get("active"), true);
//        });
        productPage = productRepository.findAll(specification, pageRequest);
        return productPage.map(ProductResponse::fromProduct);
    }

    private Product buildProduct(ProductDTO productDTO) {

        Category existedCategory =
                categoryRepository.findById(productDTO.getCategoryId())
                        .orElseThrow(() -> new DataNotFoundException("Category does not exist!"));


        // Build and return the Product entity
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .thumbnail(productDTO.getThumbnail())
                .price(productDTO.getPrice())
                .active(true)
                .categories(List.of(existedCategory))
                .isHot(productDTO.isHot())
                .quantity(productDTO.getQuantity())
                .build();
        // Prepare to map the attributes from the DTO
        List<ProductAttributeValue> attributeValues = new ArrayList<>();

        productDTO.getAttributeValues().forEach(attributeValueDTO -> {
            // Find the corresponding attribute by name
            ProductAttribute attribute = productAttributeRepository.findByName(attributeValueDTO.getAttributeName())
                    .orElseThrow(() -> new DataNotFoundException("Attribute not found: " + attributeValueDTO.getAttributeName()));

            // Create and add ProductAttributeValue entity
            ProductAttributeValue attributeValue = ProductAttributeValue.builder()
                    .productAttribute(attribute)
                    .value(attributeValueDTO.getValue())
                    .build();
            attributeValues.add(attributeValue);
            attributeValue.setProduct(newProduct);
        });
        newProduct.setAttributeValues(attributeValues);
        return newProduct;
    }
}