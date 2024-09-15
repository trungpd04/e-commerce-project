package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.model.SubCategory;
import com.nhom7.ecommercebackend.request.CategoryDTO;
import com.nhom7.ecommercebackend.request.SubCategoryDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.service.CategoryService;
import com.nhom7.ecommercebackend.service.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private CategoryDTO toCategoryDTO(Category newCategory) {
        List<SubCategoryDTO> subCategoryDTOS = new ArrayList<>();
        newCategory.getSubCategoryList().forEach(category -> {
            subCategoryDTOS.add(SubCategoryDTO.builder().name(category.getName()).build());
        });
        return CategoryDTO.builder()
                .name(newCategory.getName())
                .subCategories(subCategoryDTOS).build();
    }
}
