package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.model.SubCategory;
import com.nhom7.ecommercebackend.repository.CategoryRepository;
import com.nhom7.ecommercebackend.repository.SubCategoryRepository;
import com.nhom7.ecommercebackend.request.category.CategoryDTO;
import com.nhom7.ecommercebackend.request.category.SubCategoryDTO;
import com.nhom7.ecommercebackend.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    @Override
    @Transactional
    public Category creatCategory(CategoryDTO categoryDTO) throws DataNotFoundException {
        if(!categoryDTO.getName().isBlank() && categoryRepository.existsByName(categoryDTO.getName())) {
            throw new DataIntegrityViolationException("Category name has already exist!");
        }
        Category newCategory = Category.builder().name(categoryDTO.getName()).build();
        newCategory.setSubCategoryList(new ArrayList<>());
        categoryRepository.save(newCategory);
        return newCategory;
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                 new DataNotFoundException("Category does not exist!"));
        category.setActive(false);
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new DataNotFoundException("Category does not exist!"));
        category.setName(categoryDTO.getName());
        category.setActive(categoryDTO.isActive());
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll().stream().filter(Category::isActive).toList();
    }

    @Override
    public List<Category> getAllCategoryByAdmin() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new DataNotFoundException("Category does not exist!"));
    }

    @Override
    public Category addSubcategory(Long categoryId, SubCategoryDTO subCategoryDTO) {
        SubCategory existSubcategory = subCategoryRepository.findByName(subCategoryDTO.getSubCategoryName())
                .orElseThrow(() -> new DataNotFoundException("Subcategory does not exist!"));
        Category category = getCategoryById(categoryId);
        Category existingCategory = categoryRepository.findBySubCategoryListContaining(existSubcategory);
        if(existingCategory != null) {
            throw new DataIntegrityViolationException("Subcategory has already been added!");
        }
        category.getSubCategoryList().add(existSubcategory);
        existSubcategory.setCategory(category);
        return categoryRepository.save(category);
    }
}
