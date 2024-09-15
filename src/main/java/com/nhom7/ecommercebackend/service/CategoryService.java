package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.repository.CategoryRepository;
import com.nhom7.ecommercebackend.request.CategoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

public interface CategoryService {
    Category creatCategory(CategoryDTO categoryDTO) throws DataNotFoundException;
}
