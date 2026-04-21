package com.nhom7.ecommercebackend.request.category;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    @NotBlank(message = "Category name can not be blank")
    @Range(min = 3, message = "Category name must have at least 3 characters")
    private String name;

    private boolean active;

    private Long parentId;
}
