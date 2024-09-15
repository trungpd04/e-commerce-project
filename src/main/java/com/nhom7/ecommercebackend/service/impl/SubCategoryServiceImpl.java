package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.model.SubCategory;
import com.nhom7.ecommercebackend.repository.CategoryRepository;
import com.nhom7.ecommercebackend.repository.SubCategoryRepository;
import com.nhom7.ecommercebackend.request.SubCategoryDTO;
import com.nhom7.ecommercebackend.service.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public SubCategory createSubCategory(SubCategoryDTO subCategoryDTO) throws Exception {
        if(!subCategoryDTO.getName().isBlank() && subCategoryRepository.existsByName(subCategoryDTO.getName())) {
            throw new DataIntegrityViolationException("Sub category has already exist!");
        }
        SubCategory subCategory = SubCategory.builder().name(subCategoryDTO.getName()).build();
        subCategoryRepository.save(subCategory);
        return subCategory;
    }
}
