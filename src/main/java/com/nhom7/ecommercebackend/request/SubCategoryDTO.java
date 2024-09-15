package com.nhom7.ecommercebackend.request;

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
public class SubCategoryDTO {
    @NotBlank(message = "Sub category name can not be blank")
    @Range(min = 3, message = "Sub category name must have at least 3 characters")
    private String name;
}
