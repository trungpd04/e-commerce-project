package com.nhom7.ecommercebackend.response.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public class UserListResponse {
    @JsonProperty("user_list")
    private List<UserDetailResponse> userDetailResponses;
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
