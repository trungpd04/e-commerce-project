package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.BusinessException;
import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.repository.CategoryRepository;
import com.nhom7.ecommercebackend.request.category.CategoryDTO;
import com.nhom7.ecommercebackend.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    @CacheEvict("categories")
    public Category creatCategory(CategoryDTO categoryDTO) throws DataNotFoundException {
        Category parent = null;
        if (categoryDTO.getParentId() != null) {
            parent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new DataNotFoundException("Parent category not found"));
        }

        Category newCategory = Category.builder()
                .name(categoryDTO.getName())
                .active(categoryDTO.isActive())
                .parent(parent)
                .childrenCategories(null)
                .build();

        categoryRepository.save(newCategory);

        return newCategory;
    }

    @Override
    @Transactional
    @CacheEvict("categories")
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                 new DataNotFoundException("Category does not exist!"));
        category.setActive(false);
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    @CacheEvict("categories")
    public Category updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new DataNotFoundException("Category does not exist!"));
        if(categoryDTO.getParentId() != null) {
            if (categoryDTO.getParentId().equals(categoryId)) {
                throw new BusinessException("Category cannot be its own parent");
            }

            Category parent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new DataNotFoundException("Parent category not found"));
            if (wouldCreateCycle(parent, categoryId)) {
                throw new BusinessException("Circular category reference detected");
            }
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        category.setName(categoryDTO.getName());
        category.setActive(categoryDTO.isActive());
        return categoryRepository.save(category);
    }

    @Override
    @Cacheable("categories")
    public List<Category> getAllCategory() {
        return categoryRepository.findAll().stream().filter(Category::isActive).toList();
    }

    @Override
    @Cacheable("categories")
    public List<Category> getAllCategoryByAdmin() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new DataNotFoundException("Category does not exist!"));
    }

    @Override
    @Cacheable("categories")
    public List<Category> getCategoryTree() {
        return categoryRepository.findAllByParentIsNull();
    }

    @Cacheable(value = "categories", key = "#categoryId")
    @Override
    public List<Category> getAllCategoryChildren(Long categoryId) {
        return categoryRepository.findAllByParentId(categoryId);
    }

    private boolean wouldCreateCycle(Category current, Long targetId) {
        Category node = current;
        while (node != null) {
            if (node.getId().equals(targetId)) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }
}
