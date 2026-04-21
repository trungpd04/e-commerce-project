package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByParentIsNull();

    @Query("""
        SELECT c from Category c left join c.parent p where p.id = ?1
    """)
    List<Category> findAllByParentId(Long categoryId);
}
