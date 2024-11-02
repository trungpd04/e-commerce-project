package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.SubCategory;
import com.nhom7.ecommercebackend.request.category.SubCategoryDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.service.SubCategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.springframework.http.HttpStatus.OK;


@RestController
@RequestMapping("${api.prefix}/sub_categories")
@RequiredArgsConstructor
@Tag(name = "Subcategory")
public class SubCategoryController {
    private final SubCategoryService subCategoryService;
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse createSubCategory(@RequestBody SubCategoryDTO subCategoryDTO) throws Exception {
        SubCategory subCategory = subCategoryService.createSubCategory(subCategoryDTO);
        return ApiResponse.builder()
                .message("Create subcategory successfully!")
                .data(toSubcategoryDTO(subCategory))
                .status(HTTP_OK)
                .build();
    }
    @GetMapping("")
    public ApiResponse getAllSubcategory() {
        List<SubCategory> subCategories = subCategoryService.getAllSubcategory();
        return ApiResponse.builder()
                .message("Get all subcategory successfully!")
                .status(HTTP_OK)
                .data(subCategories)
                .build();
    }
    @GetMapping("/{subcategoryId}")
    public ApiResponse getSubcategoryById(
            @PathVariable("subcategoryId")  Long subcategoryId
    ) {
        return ApiResponse.builder()
                .message("Get all subcategory successfully!")
                .status(HTTP_OK)
                .data(toSubcategoryDTO(subCategoryService.getSubcategoryById(subcategoryId)))
                .build();
    }
    @PutMapping("/{subcategoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse updateSubcategory(
            @PathVariable("subcategoryId") Long subCategoryId,
            @RequestBody SubCategoryDTO subCategoryDTO

    ) {
        SubCategory subCategory = subCategoryService.updateSubcategory(subCategoryId,subCategoryDTO);
        return ApiResponse.builder()
                .message("Update subcategory successfully!")
                .status(HTTP_OK)
                .data(toSubcategoryDTO(subCategory))
                .build();
    }
    @DeleteMapping("/{subcategoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse deleteSubcategory(@PathVariable Long subcategoryId) {
        SubCategory subCategory = subCategoryService.deleteSubcategoryById(subcategoryId);
        return ApiResponse.builder()
                .message("Delete subcategory successfully!")
                .status(HTTP_OK)
                .build();
    }
    public SubCategoryDTO toSubcategoryDTO(SubCategory subCategory) {
        return SubCategoryDTO.builder()
                .subCategoryName(subCategory.getName())
                .build();
    }
    public List<SubCategoryDTO> toSubcategoryDTOList(List<SubCategory> subCategories) {
        List<SubCategoryDTO> subCategoryDTOS = new ArrayList<>();
        subCategories.forEach(subCategory -> {
            subCategoryDTOS.add(SubCategoryDTO.builder().subCategoryName(subCategory.getName()).build());
        });
        return subCategoryDTOS;
    }
}
