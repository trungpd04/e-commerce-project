package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.exception.InvalidParamException;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.ProductImage;
import com.nhom7.ecommercebackend.request.ProductDTO;
import com.nhom7.ecommercebackend.request.ProductImageDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.service.ProductService;
import com.nhom7.ecommercebackend.utils.FileUtils;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
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
    @PostMapping(value = "/uploads/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse uploadImage(
            @PathVariable("productId") Long productId,
            @ModelAttribute List<MultipartFile> files
    ) throws IOException, InvalidParamException {
        Product existingProduct = productService.getProductById(productId);
        files = files == null ? new ArrayList<MultipartFile>() : files;
        if(files.size() > 5) {
            return ApiResponse.builder()
                    .status(HTTP_BAD_REQUEST)
                    .message("One product have max 5 images")
                    .build();
        }
        List<ProductImage> productImages = new ArrayList<>();
        for(MultipartFile file : files) {
            if(file.getSize() == 0) {
                continue;
            }
            if(file.getSize() > 10 * 1024 * 1024) { // Kích thước > 10MB
                return ApiResponse.builder()
                        .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
                        .message("File too large! > 10mb")
                        .build();
            }
            String contentType = file.getContentType();
            if(contentType == null || !contentType.startsWith("image/")) {
                return ApiResponse.builder()
                        .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                        .message("File is not a image!")
                        .build();
            }
            String fileName = FileUtils.storeFile(file);
            ProductImage productImage = productService.createProductImage(
                    productId,
                    ProductImageDTO.builder().imageUrl(fileName).build()
                    );
            productImages.add(productImage);
        }
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Upload image successfully!")
                .data(productImages)
                .build();

    }
    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> getImage(@PathVariable String imageName) throws MalformedURLException {
        try{
            Path path = Paths.get("uploads/" + imageName);
            UrlResource urlResource = new UrlResource(path.toUri());
            if(urlResource.exists()) {
                return ResponseEntity.ok()
                        .contentType(FilenameUtils.getExtension(imageName).equalsIgnoreCase("jpg") ? MediaType.IMAGE_JPEG : MediaType.IMAGE_PNG)
                        .body(urlResource);
            }else{
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpeg").toUri()));
            }
        }catch (Exception e) {
            return ResponseEntity.notFound().build();
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
