package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.request.ProductDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ApiResponse getAllProducts() {
        List<ProductDTO> productDTOList = productService.getAllProducts()
                .stream()
                .map(this::toProductDTO)
                .collect(Collectors.toList());
        return ApiResponse.builder()
                .data(productDTOList)
                .message("Fetched all products successfully!")
                .status(HTTP_OK)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return ApiResponse.builder()
                    .data(toProductDTO(product))
                    .message("Fetched product successfully!")
                    .status(HTTP_OK)
                    .build();
        } else {
            return ApiResponse.builder()
                    .message("Product not found!")
                    .status(HTTP_OK)
                    .build();
        }
    }

    @PostMapping
    public ApiResponse createProduct(@RequestBody ProductDTO productDTO) {
        Product newProduct = productService.saveProduct(toProductEntity(productDTO));
        return ApiResponse.builder()
                .data(toProductDTO(newProduct))
                .message("Create Product successfully!")
                .status(HTTP_OK)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.builder()
                .message("Product deleted successfully!")
                .status(HTTP_OK)
                .build();
    }

    // Converts Product entity to ProductDTO
    private ProductDTO toProductDTO(Product product) {
        return ProductDTO.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .build();
    }

    // Converts ProductDTO to Product entity
    private Product toProductEntity(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setThumbnail(productDTO.getThumbnail());
        return product;
    }
}
