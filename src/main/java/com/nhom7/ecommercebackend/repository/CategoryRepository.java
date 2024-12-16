package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
    boolean existsByName(String name);
    Category findBySubCategoryList(List<SubCategory> subCategories);
    Category findBySubCategoryListContaining(SubCategory subCategory);
}
