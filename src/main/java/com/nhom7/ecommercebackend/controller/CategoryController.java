package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.request.category.CategoryDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.response.category.CategoryTreeResponse;
import com.nhom7.ecommercebackend.service.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse createCategory(@RequestBody CategoryDTO categoryDTO) {
        Category newCategory = categoryService.creatCategory(categoryDTO);
        return ApiResponse.builder()
                .data(CategoryTreeResponse.fromCategory(newCategory))
                .message("Create Category successfully!")
                .status(HTTP_OK)
                .build();
    }

    @GetMapping("/{categoryId}")
    public ApiResponse getCategoryById(@PathVariable("categoryId") Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .data(CategoryTreeResponse.fromCategory(category))
                .message("Get category by Id successfully!")
                .build();
    }

    @GetMapping("")
    public ApiResponse getAllActiveCategory() {
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Get all category successfully!")
                .data(
                        categoryService
                                .getAllCategory()
                                .stream()
                                .filter(Category::isActive)
                                .map(CategoryTreeResponse::fromCategory)
                                .toList()
                )
                .build();

    }

    @GetMapping("/tree")
    public ApiResponse getCategoryTree() {
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Get all category successfully!")
                .data(
                        categoryService
                                .getCategoryTree()
                                .stream()
                                .filter(Category::isActive)
                                .map(CategoryTreeResponse::fromCategory)
                                .toList()
                )
                .build();

    }

    @GetMapping("/{categoryId}/children")
    public ApiResponse getCategoryChildrenByParentId(@PathVariable Long categoryId) {
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Get all category successfully!")
                .data(
                        categoryService
                                .getAllCategoryChildren(categoryId)
                                .stream()
                                .filter(Category::isActive)
                                .map(CategoryTreeResponse::fromCategory)
                                .toList()
                )
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse getAllCategory() {
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Get all category by admin successfully!")
                .data(
                        categoryService
                        .getAllCategoryByAdmin().stream()
                        .map(CategoryTreeResponse::fromCategory)
                        .toList()
                )
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{categoryId}")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse deleteCategoryById(@PathVariable("categoryId") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Deactivate category successfully!")
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
                .data(CategoryTreeResponse.fromCategory(category))
                .message("Update category successfully!")
                .build();
    }
}
