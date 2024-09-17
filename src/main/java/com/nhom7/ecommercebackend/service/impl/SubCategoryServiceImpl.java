package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.SubCategory;
import com.nhom7.ecommercebackend.repository.CategoryRepository;
import com.nhom7.ecommercebackend.repository.ProductRepository;
import com.nhom7.ecommercebackend.repository.SubCategoryRepository;
import com.nhom7.ecommercebackend.request.SubCategoryDTO;
import com.nhom7.ecommercebackend.service.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public SubCategory createSubCategory(SubCategoryDTO subCategoryDTO) throws Exception {
        if(!subCategoryDTO.getSubCategoryName().isBlank()
                && subCategoryRepository.existsByName(subCategoryDTO.getSubCategoryName())) {
            throw new DataIntegrityViolationException("Subcategory has already existed!");
        }
        if(!subCategoryDTO.getSubCategoryName().isBlank()
                && categoryRepository.existsByName(subCategoryDTO.getSubCategoryName())) {
            throw new DataIntegrityViolationException("Subcategory can not be same with category name");
        }
        SubCategory subCategory = SubCategory.builder().name(subCategoryDTO.getSubCategoryName()).build();
        return subCategoryRepository.save(subCategory);
    }

    @Override
    public SubCategory getSubcategoryById(Long subcategoryId) {
        return subCategoryRepository.findById(subcategoryId)
                .orElseThrow(() -> new DataNotFoundException("Subcategory does not exist!"));
    }

    @Override
    public List<SubCategory> getAllSubcategory() {
        return subCategoryRepository.findAll();
    }

    @Override
    public SubCategory updateSubcategory(Long subCategoryId, SubCategoryDTO subCategoryDTO) {
        SubCategory existingSubCategory = subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new DataNotFoundException("Subcategory does not exist!"));
        existingSubCategory.setName(subCategoryDTO.getSubCategoryName());
        return subCategoryRepository.save(existingSubCategory);
    }

    @Override
    public SubCategory deleteSubcategoryById(Long subcategoryId) {
        SubCategory subCategory = subCategoryRepository.findById(subcategoryId)
                .orElseThrow(() -> new DataNotFoundException("Subcategory does not exist!"));
        List<Product> products = productRepository.findBySubcategory(subCategory);
        if(!products.isEmpty()) {
            throw new IllegalStateException("Can not delete subcategory with associated product");
        }else{
            Category category = subCategory.getCategory();

            if (category != null) {
                // Step 2: Remove the subCategory from the category's list
                category.getSubCategoryList().remove(subCategory);

                // Step 3: Save the category to update the relationship
                categoryRepository.save(category);
            }

            // Step 4: Now proceed to delete the subCategory
            subCategoryRepository.delete(subCategory);

            return subCategory;
        }
    }
}
