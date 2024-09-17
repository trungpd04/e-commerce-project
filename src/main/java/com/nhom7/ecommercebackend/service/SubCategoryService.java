package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.model.SubCategory;
import com.nhom7.ecommercebackend.repository.SubCategoryRepository;
import com.nhom7.ecommercebackend.request.SubCategoryDTO;

import java.util.List;

public interface SubCategoryService {
    SubCategory createSubCategory(SubCategoryDTO subCategoryDTO) throws Exception;
    SubCategory getSubcategoryById(Long subcategoryId);
    List<SubCategory> getAllSubcategory();

    SubCategory updateSubcategory(Long subCategoryId, SubCategoryDTO subCategoryDTO);

    SubCategory deleteSubcategoryById(Long subcategoryId);
}
