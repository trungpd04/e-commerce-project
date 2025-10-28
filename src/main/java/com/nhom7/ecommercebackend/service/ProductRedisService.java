package com.nhom7.ecommercebackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nhom7.ecommercebackend.repository.filter.Filter;
import com.nhom7.ecommercebackend.response.product.ProductResponse;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProductRedisService {
    void saveListProductsFilterToCache(
            List<ProductResponse> products,
            Filter filter,
            PageRequest pageRequest,
            String sortBy
    ) throws JsonProcessingException;
    List<ProductResponse> getCachedProducts(Filter filter, PageRequest pageRequest, String sortBy) throws JsonProcessingException;
}
