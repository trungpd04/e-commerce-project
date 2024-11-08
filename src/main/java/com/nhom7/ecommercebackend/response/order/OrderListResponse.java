package com.nhom7.ecommercebackend.response.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class OrderListResponse {
    private List<OrderResponse> orders;
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
