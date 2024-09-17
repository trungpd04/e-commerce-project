package com.nhom7.ecommercebackend.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;


import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoryDTO {

    @JsonProperty("name")
    @NotBlank(message = "Subcategory name can not be blank!")
    @Range(min = 3, message = "Subcategory name must has at least 3 characters!")
    private String subCategoryName;
}
