package com.nhom7.ecommercebackend.response.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nhom7.ecommercebackend.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryTreeResponse {
    private Long id;
    private String name;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<CategoryTreeResponse> children;

    public static CategoryTreeResponse fromCategory(Category category) {
        return CategoryTreeResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .children(category.getChildrenCategories().stream().map(CategoryTreeResponse::fromCategory).toList())
                .build();
    }
}
