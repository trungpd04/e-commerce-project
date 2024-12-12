package com.nhom7.ecommercebackend.response;

import com.nhom7.ecommercebackend.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryResponse {
    private Long id;
    private String name;

    public CategoryResponse fromCategory(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
