package com.nhom7.ecommercebackend.response.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductListResponse {
    @JsonProperty("product_response")
    List<ProductResponse> productResponses;
    @JsonProperty("page_no")
    private int pageNo;
    @JsonProperty("page_size")
    private int pageSize;
    @JsonProperty("total_elements")
    private long totalElements;
    @JsonProperty("total_pages")
    private int totalPages;
    private boolean last;
}
