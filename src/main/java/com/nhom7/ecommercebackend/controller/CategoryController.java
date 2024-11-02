package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.request.category.CategoryDTO;
import com.nhom7.ecommercebackend.request.category.SubCategoryDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.service.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse createCategory(@RequestBody CategoryDTO categoryDTO) throws Exception {
        Category newCategory = categoryService.creatCategory(categoryDTO);
        return ApiResponse.builder()
                .data(newCategory)
                .message("Create Category successfully!")
                .status(HTTP_OK)
                .build();
    }

    @GetMapping("/{categoryId}")
    public ApiResponse getCategoryById(@PathVariable("categoryId") Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .data(category)
                .message("Get category by Id successfully!")
                .build();
    }
    @GetMapping("")
    public ApiResponse getAllCategory() {
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Get all category successfully!")
                .data(categoryService.getAllCategory())
                .build();

    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{categoryId}")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse deleteCategoryById(@PathVariable("categoryId") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Get category by Id successfully!")
                .build();
    }
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse updateCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestBody CategoryDTO categoryDTO
            ) {
        Category category = categoryService.updateCategory(categoryId, categoryDTO);
        return ApiResponse.builder()
                .status(HTTP_OK)
//                .data(toCategoryDTO(category))
                .message("Update category successfully!")
                .build();

    }
    @PostMapping("/add_subcategory/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse addSubcategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestBody SubCategoryDTO subCategoryDTO
    ) {
        Category category = categoryService.addSubcategory(categoryId, subCategoryDTO);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .data(category)
                .message("Add subcategory successfully!")
                .build();
    }
}
