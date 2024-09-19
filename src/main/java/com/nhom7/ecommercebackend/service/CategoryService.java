package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.repository.CategoryRepository;
import com.nhom7.ecommercebackend.request.CategoryDTO;
import com.nhom7.ecommercebackend.request.SubCategoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CategoryService {
    Category creatCategory(CategoryDTO categoryDTO) throws DataNotFoundException;
    void deleteCategory(Long categoryId);
    Category updateCategory(Long categoryId, CategoryDTO categoryDTO);
    List<Category> getAllCategory();
    Category getCategoryById(Long categoryId);
    Category addSubcategory(Long categoryId, SubCategoryDTO subCategoryDTO);
}
