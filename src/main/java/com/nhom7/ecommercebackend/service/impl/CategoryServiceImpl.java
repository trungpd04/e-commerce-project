package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.model.SubCategory;
import com.nhom7.ecommercebackend.repository.CategoryRepository;
import com.nhom7.ecommercebackend.repository.SubCategoryRepository;
import com.nhom7.ecommercebackend.request.CategoryDTO;
import com.nhom7.ecommercebackend.request.SubCategoryDTO;
import com.nhom7.ecommercebackend.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        List<SubCategory> subCategories = new ArrayList<>();
        categoryDTO.getSubCategories().forEach(subCategoryDTO -> {
            if(!subCategoryDTO.getName().isBlank() && subCategoryRepository.existsByName(subCategoryDTO.getName())) {
                throw new DataIntegrityViolationException("Sub category " + subCategoryDTO.getName() + " has already exist!");
            }
           SubCategory subCategory = SubCategory.builder().name(subCategoryDTO.getName()).build();
           subCategory.setCategory(newCategory);
           subCategories.add(subCategory);
        });
        newCategory.setSubCategoryList(subCategories);
        categoryRepository.save(newCategory);
        return newCategory;
    }
}
