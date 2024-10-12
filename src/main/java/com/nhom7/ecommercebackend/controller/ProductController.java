package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.exception.InvalidParamException;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.ProductImage;
import com.nhom7.ecommercebackend.repository.filter.Filter;
import com.nhom7.ecommercebackend.request.product.ProductDTO;
import com.nhom7.ecommercebackend.request.product.ProductDetailDTO;
import com.nhom7.ecommercebackend.request.product.ProductImageDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.response.product.ProductDetailResponse;
import com.nhom7.ecommercebackend.response.product.ProductListResponse;
import com.nhom7.ecommercebackend.response.product.ProductResponse;
import com.nhom7.ecommercebackend.service.ProductService;
import com.nhom7.ecommercebackend.utils.FileUtils;
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
import java.util.Objects;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("")
    public ApiResponse getAllProductsFilter(
            @RequestParam(value = "search", required = false) String keyword,
            @RequestParam(value = "category_id", required = false) Integer categoryId,
            @RequestParam(value = "subcategory_id", required = false) Integer subCategoryId,
            @RequestParam(value = "ram", required = false) String ramRange,
            @RequestParam(value = "storage", required = false) String storage,
            @RequestParam(value = "price", required = false) String price,
            @RequestParam(value = "screen_size", required = false) String screenSize,
            @RequestParam(value = "screen_type", required = false) String screenType,
            @RequestParam(value = "screen_refresh_rate", required = false) String refreshRate,
            @RequestParam(value = "rate", required = false) Integer rate,
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "5", required = false) Integer size,
            @RequestParam(value = "sort_by", required = false, defaultValue = "id") String sortBy,
            @RequestParam(value = "sort_dir", required = false) String sortDir
    ) {
        Sort.Direction sortDirection = Objects.equals(sortDir, "DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Filter filter = new Filter(
                keyword, categoryId, subCategoryId,price, ramRange, screenType, screenSize, refreshRate, storage, rate);
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
                .data(toProductDTO(newProduct))
                .message("Create Product successfully!")
                .status(HTTP_OK)
                .build();
    }
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse updateProduct(
            @PathVariable("productId") Long productId,
            @RequestBody ProductDTO productDTO
            ) {
        Product product = productService.updateProduct(productId, productDTO);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Update product successfully!")
                .data(ProductResponse.fromProduct(product))
                .build();
    }
    @PostMapping(value = "/uploads/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearer-key")
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
            Path path = Paths.get(STR."uploads/\{imageName}");
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

    // Converts Product entity to ProductDTO
    private ProductDTO toProductDTO(Product product) {
        Long categoryId = product.getSubcategory().getFirst().getCategory().getId();
        List<Long> subcategoriesId = new ArrayList<>();
        product.getSubcategory().forEach(subcategory -> {
            subcategoriesId.add(subcategory.getId());
        });
        ProductDetailDTO productDetailDTO = ProductDetailDTO.builder()
                .cpu(product.getProductDetail().getCpu())
                .ram(product.getProductDetail().getRam())
                .screenSize(product.getProductDetail().getScreenSize())
                .screenType(product.getProductDetail().getScreenType())
                .screenRefreshRate(product.getProductDetail().getScreenRefreshRate())
                .osName(product.getProductDetail().getOsName())
                .designDescription(product.getProductDetail().getDesignDescription())
                .batteryCapacity(product.getProductDetail().getBatteryCapacity())
                .guaranteeMonth(product.getProductDetail().getGuaranteeMonth())
                .color(product.getProductDetail().getColor())
                .quantity(product.getProductDetail().getQuantity())
                .manufacturer(product.getProductDetail().getManufacturer())
                .build();
        return ProductDTO.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .categoryId(categoryId)
                .subcategory(subcategoriesId)
                .productDetailDTO(productDetailDTO)
                .build();
    }
}
