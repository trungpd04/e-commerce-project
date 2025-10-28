package com.nhom7.ecommercebackend.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class    PaginationResponse<T> {
    private int page;
    private int pageSize;
    private int totalPage;
    private long totalItems;
    private boolean last;
    private List<T> items;
}
