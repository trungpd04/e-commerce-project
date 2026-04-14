package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.request.category.CategoryDTO;

import java.util.List;

public interface CategoryService {
    Category creatCategory(CategoryDTO categoryDTO) throws DataNotFoundException;
    void deleteCategory(Long categoryId);
    Category updateCategory(Long categoryId, CategoryDTO categoryDTO);
    List<Category> getAllCategory();
    List<Category> getAllCategoryByAdmin();
    Category getCategoryById(Long categoryId);
    List<Category> getCategoryTree();
    List<Category> getAllCategoryChildren(Long categoryId);
}
