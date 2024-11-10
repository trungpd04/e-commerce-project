package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.exception.InvalidParamException;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.ProductImage;
import com.nhom7.ecommercebackend.repository.filter.Filter;
import com.nhom7.ecommercebackend.request.product.ProductDTO;
import com.nhom7.ecommercebackend.request.product.ProductImageDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.response.product.ProductDetailResponse;
import com.nhom7.ecommercebackend.response.product.ProductListResponse;
import com.nhom7.ecommercebackend.response.product.ProductResponse;
import com.nhom7.ecommercebackend.service.ProductService;
import com.nhom7.ecommercebackend.utils.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.springframework.http.HttpStatus.*;


@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("")
    @Operation(summary = "Get all products with optional filters")
    public ApiResponse getAllProductsFilter(
            @Parameter(
                    description = "Lọc sản phẩm theo 1 hoặc nhiều thuộc tính. ( Dùng account Admin để xem tất cả các thuộc tính hiện có  ) " + "\n" +
                            "Example keys: `mobile_storage`, `laptop_ram`, `category_id`, `subcategory_id`. " + "\n" +
                            "Có thể lọc theo khoảng hoặc có thể lọc theo giá trị cố định tùy vào mục đích và từng thuộc tính. " + "\n" +
                            "Example: `{ \"mobile_ram\" : \"4-10\" }`, `{ \"mobile_storage\" : \"128\" }`, `{ \"laptop_ram\" : \"16\" }`, `{ \"category_id\" : \"1\" }`, `{ \"subcategory_id\" : \"1\" }`.",
                    in = ParameterIn.QUERY,
                    name = "attributeValue"
//                    example = "{ \"mobile_ram\" : \"4-10\" }"
            )
            @RequestParam(defaultValue = "") Map<String, String> attributeValue,
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "5", required = false) Integer size,
            @RequestParam(value = "sort_by", required = false, defaultValue = "id") String sortBy,
            @RequestParam(value = "sort_dir", required = false, defaultValue = "asc") String sortDir
    ) {
        PageRequest pageRequest = null;
        Sort.Direction sortDirection = sortDir.trim().equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        if(attributeValue.get("sort_by").equalsIgnoreCase("rating")) {
            Sort sort = Sort.by(sortDirection, sortBy.trim().toLowerCase());
            pageRequest = PageRequest.of(page, size);
        }else{
            Sort sort = Sort.by(sortDirection, sortBy.trim().toLowerCase());
            pageRequest = PageRequest.of(page, size, sort);
        }

        Filter filter = new Filter(attributeValue);
        Page<ProductResponse> productResponses = productService
                .getAllProductFilter(filter, pageRequest);
        int pageNo = productResponses.getNumber();
        int pageSize = productResponses.getSize();
        int totalPages = productResponses.getTotalPages();
        long totalElements = productResponses.getTotalElements();
        boolean last = productResponses.isLast();
        ProductListResponse productListResponse = ProductListResponse
                .builder()
                .productResponses(productResponses.getContent())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .last(last)
                .build();
        return ApiResponse.builder()
                .message("Fetch product successfully!")
                .status(HTTP_OK)
                .data(productListResponse)
                .build();
    }

    @GetMapping("/details/{productId}")
    public ApiResponse getProductDetailById(@PathVariable("productId") Long productId) {
        Product product = productService.getProductById(productId);
        return ApiResponse.builder()
                .data(ProductDetailResponse.fromProduct(product))
                .message("Fetched product successfully!")
                .status(HTTP_OK)
                .build();
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse createProduct(@RequestBody ProductDTO productDTO) {
        Product newProduct = productService.createProduct(productDTO);
        return ApiResponse.builder()
                .data(ProductDetailResponse.fromProduct(newProduct))
                .message("Create Product successfully!")
                .status(HTTP_OK)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @PutMapping("/{productId}")
    public ApiResponse updateProduct(
            @PathVariable("productId") Long productId,
            @RequestBody ProductDTO productDTO
    ) {
        Product updatedProduct = productService.updateProduct(productId, productDTO);
        return ApiResponse.builder()
                .message("Fetch product successfully!")
                .status(HTTP_OK)
                .data(ProductDetailResponse.fromProduct(updatedProduct))
                .build();
    }

    @PostMapping(value = "/uploads/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse uploadImage(
            @PathVariable("productId") Long productId,
            @ModelAttribute List<MultipartFile> files
    ) throws IOException, InvalidParamException {
        files = files == null ? new ArrayList<MultipartFile>() : files;
        if (files.size() > 5) {
            return ApiResponse.builder()
                    .status(BAD_REQUEST.value())
                    .message("One product have max 5 images")
                    .build();
        }
        List<ProductImage> productImages = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.getSize() == 0) {
                continue;
            }
            if (file.getSize() > 10 * 1024 * 1024) { // Kích thước > 10MB
                return ApiResponse.builder()
                        .status(PAYLOAD_TOO_LARGE.value())
                        .message("File too large! > 10mb")
                        .build();
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ApiResponse.builder()
                        .status(UNSUPPORTED_MEDIA_TYPE.value())
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
        try {
            Path path = Paths.get("uploads/" + imageName );
            UrlResource urlResource = new UrlResource(path.toUri());
            if (urlResource.exists()) {
                return ResponseEntity.ok()
                        .contentType(FilenameUtils.getExtension(imageName).equalsIgnoreCase("jpg") ? MediaType.IMAGE_JPEG : MediaType.IMAGE_PNG)
                        .body(urlResource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpeg").toUri()));
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.builder()
                .message("Product deleted successfully!")
                .status(HTTP_OK)
                .build();
    }
}
