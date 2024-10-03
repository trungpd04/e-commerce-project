package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.exception.InvalidParamException;
import com.nhom7.ecommercebackend.model.*;
import com.nhom7.ecommercebackend.repository.*;
import com.nhom7.ecommercebackend.request.ProductDTO;
import com.nhom7.ecommercebackend.request.ProductDetailDTO;
import com.nhom7.ecommercebackend.request.ProductImageDTO;
import com.nhom7.ecommercebackend.response.ProductResponse;
import com.nhom7.ecommercebackend.service.ProductService;
import com.nhom7.ecommercebackend.utils.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {

        if (!productDTO.getName().isBlank() && productRepository.existsByName(productDTO.getName())) {
            throw new DataIntegrityViolationException("Product name already exists!");
        }
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category does not exist!"));
        Product newProduct = buildProduct(productDTO);
        return productRepository.save(newProduct);
    }
    @Override
    public Product getProductById(Long productId) throws DataNotFoundException {
        return productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product not found for ID: " + productId));
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) throws DataNotFoundException {
        Product product = productRepository.findProductById(productId)
                        .orElseThrow(() -> new DataNotFoundException("Product not found for ID: " + productId));
        product.setActive(false); // xóa mềm
        productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long productId, ProductDTO productDTO) {
        Product product = productRepository.findProductById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product does not exist!"));
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category does not exist!"));
        List<SubCategory> subCategories = new ArrayList<>();
        productDTO.getSubcategory().forEach(subCategoryId -> {
            SubCategory subCategory = subCategoryRepository.findById(subCategoryId)
                    .orElseThrow(() -> new DataNotFoundException("Sub category not found!"));
            subCategories.add(subCategory);
        });
        ProductDetail productDetail = ProductDetail.builder()
                .cpu(productDTO.getProductDetailDTO().getCpu())
                .ram(productDTO.getProductDetailDTO().getRam())
                .screenSize(productDTO.getProductDetailDTO().getScreenSize())
                .screenType(productDTO.getProductDetailDTO().getScreenType())
                .screenRefreshRate(productDTO.getProductDetailDTO().getScreenRefreshRate())
                .osName(productDTO.getProductDetailDTO().getOsName())
                .batteryCapacity(productDTO.getProductDetailDTO().getBatteryCapacity())
                .guaranteeMonth(productDTO.getProductDetailDTO().getGuaranteeMonth())
                .manufacturer(productDTO.getProductDetailDTO().getManufacturer())
                .color(productDTO.getProductDetailDTO().getColor())
                .quantity(productDTO.getProductDetailDTO().getQuantity())
                .designDescription(productDTO.getProductDetailDTO().getDesignDescription())
                .build();
        product.setProductDetail(productDetail);
        product.setName(product.getName());
        product.setPrice(product.getPrice());
        product.setThumbnail(product.getThumbnail());
        product.setDescription(product.getDescription());
        product.setSubcategory(subCategories);
        return productRepository.save(product);
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

    @Override
    public Page<ProductResponse> getAllProduct(String keyword, Integer categoryId, Integer subcategoryId,  PageRequest pageRequest) {
        Page<Product> productPage;
        productPage = productRepository.getAllProducts(keyword, categoryId, subcategoryId, pageRequest);
        return productPage.map(ProductResponse::fromProduct);
    }
    @Override
    public Page<ProductResponse> getAllProductFilter(Filter filter, PageRequest pageRequest) {
        Page<Product> productPage;
        Specification<Product> specification = new FilterSpecification<>(filter);
        productPage = productRepository.findAll(specification, pageRequest);
        return productPage.map(ProductResponse::fromProduct);
    }

    private Product buildProduct(ProductDTO productDTO, Long... productId) {
        List<SubCategory> subCategories = new ArrayList<>();
        productDTO.getSubcategory().forEach(subCategoryId -> {
            SubCategory subCategory = subCategoryRepository.findById(subCategoryId)
                    .orElseThrow(() -> new DataNotFoundException("Sub category not found!"));
            subCategories.add(subCategory);
        });
        ProductDetail productDetail = ProductDetail.builder()
                .cpu(productDTO.getProductDetailDTO().getCpu())
                .ram(productDTO.getProductDetailDTO().getRam())
                .screenSize(productDTO.getProductDetailDTO().getScreenSize())
                .screenType(productDTO.getProductDetailDTO().getScreenType())
                .screenRefreshRate(productDTO.getProductDetailDTO().getScreenRefreshRate())
                .osName(productDTO.getProductDetailDTO().getOsName())
                .batteryCapacity(productDTO.getProductDetailDTO().getBatteryCapacity())
                .guaranteeMonth(productDTO.getProductDetailDTO().getGuaranteeMonth())
                .manufacturer(productDTO.getProductDetailDTO().getManufacturer())
                .color(productDTO.getProductDetailDTO().getColor())
                .quantity(productDTO.getProductDetailDTO().getQuantity())
                .designDescription(productDTO.getProductDetailDTO().getDesignDescription())
                .build();
        return Product.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .subcategory(subCategories)
                .productDetail(productDetail)
                .build();
    }

}
