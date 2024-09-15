package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.model.SubCategory;
import com.nhom7.ecommercebackend.repository.SubCategoryRepository;
import com.nhom7.ecommercebackend.request.SubCategoryDTO;

public interface SubCategoryService {
    SubCategory createSubCategory(SubCategoryDTO subCategoryDTO) throws Exception;
}
