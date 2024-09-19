package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.model.SubCategory;
import com.nhom7.ecommercebackend.request.CategoryDTO;
import com.nhom7.ecommercebackend.request.SubCategoryDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.service.CategoryService;
import com.nhom7.ecommercebackend.service.SubCategoryService;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @PostMapping
    public ApiResponse createCategory(@RequestBody CategoryDTO categoryDTO) throws Exception {
        Category newCategory = categoryService.creatCategory(categoryDTO);
        return ApiResponse.builder()
                .data(toCategoryDTO(newCategory))
                .message("Create Category successfully!")
                .status(HTTP_OK)
                .build();
    }

    @GetMapping("/{categoryId}")
    public ApiResponse getCategoryById(@PathVariable("categoryId") Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .data(toCategoryDTO(category))
                .message("Get category by Id successfully!")
                .build();
    }
    @GetMapping("")
    public ApiResponse getAllCategory() {
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Get all category successfully!")
                .data(toListCategoryDTO(categoryService.getAllCategory()))
                .build();
    }
    @DeleteMapping("/{categoryId}")
    public ApiResponse deleteCategoryById(@PathVariable("categoryId") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Get category by Id successfully!")
                .build();
    }
    @PutMapping("/{categoryId}")
    public ApiResponse updateCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestBody CategoryDTO categoryDTO
            ) {
        Category category = categoryService.updateCategory(categoryId, categoryDTO);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .data(toCategoryDTO(category))
                .message("Update category successfully!")
                .build();

    }
    @PostMapping("/add_subcategory/{categoryId}")
    public ApiResponse addSubcategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestBody SubCategoryDTO subCategoryDTO
    ) {
        Category category = categoryService.addSubcategory(categoryId, subCategoryDTO);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .data(toCategoryDTO(category))
                .message("Add subcategory successfully!")
                .build();
    }
    private CategoryDTO toCategoryDTO(Category newCategory) {
        if(!newCategory.getSubCategoryList().isEmpty()) {
            List<String> subCategoryName = new ArrayList<>();
            newCategory.getSubCategoryList().forEach(subCategory -> {
                subCategoryName.add(subCategory.getName());
            });
            return CategoryDTO.builder()
                    .name(newCategory.getName())
                    .subCategories(subCategoryName)
                    .build();
        }
        return CategoryDTO.builder()
                .name(newCategory.getName()).subCategories(null).build();
    }
    private List<CategoryDTO> toListCategoryDTO(List<Category> categories) {
        List<CategoryDTO>  subCategoryDTOS = new ArrayList<>();
        categories.forEach(category -> {
            subCategoryDTOS.add(toCategoryDTO(category));
        });
        return subCategoryDTOS;
    }
}
