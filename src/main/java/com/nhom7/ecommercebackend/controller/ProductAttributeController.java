package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.ProductAttribute;
import com.nhom7.ecommercebackend.request.product.AttributeDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.service.ProductAttributeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("${api.prefix}/attributes")
@RequiredArgsConstructor
public class ProductAttributeController {

    private final ProductAttributeService attributeService;

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse createProductAttribute(@RequestBody AttributeDTO attributeDTO) {
        ProductAttribute newProductAttribute = attributeService.createProductAttribute(attributeDTO);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Create product attribute successfully!")
                .data(newProductAttribute)
                .build();
    }

    @GetMapping("")
    public ApiResponse getAllProductAttribute(
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProductAttribute> productAttributes = attributeService.getAll(pageRequest);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Get all product attribute successfully!")
                .data(productAttributes)
                .build();
    }
    @GetMapping("/{attributeId}")
    @SecurityRequirement(name = "bearer-key")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse getAttributeById(@PathVariable("attributeId") int attributeId) {
        ProductAttribute productAttribute = attributeService.getProductAttribute(attributeId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Get product attribute successfully!")
                .data(productAttribute)
                .build();
    }
    @PutMapping("/{attributeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse updateAttributeById(
            @PathVariable("attributeId") int attributeId,
            @RequestBody AttributeDTO attributeDTO) {
        ProductAttribute productAttribute = attributeService.updateAttributeById(attributeId, attributeDTO);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Update product attribute successfully!")
                .data(productAttribute)
                .build();
    }
    @DeleteMapping("/{attributeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse deleteProductAttributeById(@PathVariable("attributeId") int attributeId) {
        attributeService.deleteAttribute(attributeId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Delete product attribute successfully!")
                .build();
    }
}
