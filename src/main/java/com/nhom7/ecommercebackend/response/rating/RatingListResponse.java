package com.nhom7.ecommercebackend.response.rating;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public class RatingListResponse {
    @JsonProperty("rate_response")
    List<RatingResponse> ratingResponses;
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
