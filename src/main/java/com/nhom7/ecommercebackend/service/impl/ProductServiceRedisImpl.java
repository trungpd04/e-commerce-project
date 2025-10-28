package com.nhom7.ecommercebackend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhom7.ecommercebackend.repository.filter.Filter;
import com.nhom7.ecommercebackend.response.product.ProductResponse;
import com.nhom7.ecommercebackend.service.ProductRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceRedisImpl implements ProductRedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void saveListProductsFilterToCache(
            List<ProductResponse> products,
            Filter filter,
            PageRequest pageRequest,
            String sortBy
            )
            throws JsonProcessingException
        {
            String key = getKeyFromMap(filter.getAttributeValueMap(),pageRequest, sortBy);
            redisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(products)
            );
        }

    @Override
    public List<ProductResponse> getCachedProducts (
            Filter filter, PageRequest pageRequest, String sortBy
    ) throws JsonProcessingException {
            if (!filter.getAttributeValueMap().isEmpty()) {
                String key = getKeyFromMap(filter.getAttributeValueMap(),pageRequest, sortBy);
                String value = null;
                try {
                    value = (String) redisTemplate.opsForValue().get(key);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if(value == null) {
                    return null;
                }else{
                    List<ProductResponse> products = objectMapper.readValue(value, new TypeReference<List<ProductResponse>>() {

                    });
                    return products;
                }
            } else {
                return null;
            }
    }
    private String getKeyFromMap(Map<String, String> filter, PageRequest pageRequest, String sortBy) {
        Stream<Sort.Order> orders = pageRequest.getSort().stream();
        Sort sort = pageRequest.getSort();
        String sortDirection = sort.getOrderFor(sortBy)
                .getDirection() == Sort.Direction.ASC ? "asc": "desc";
        log.info("key: {}", String.format(
                "PRODUCT_FILTERED:%s:%s:%d:%d:%s",
                filter.get("search"),
                filter.get("rating"),
                pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                sortDirection
        ));
        return String.format(
                "PRODUCT_FILTERED:%s:%s:%d:%d:%s",
                filter.get("search"),
                filter.get("rating"),
                pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                sortDirection
                );
    }
}
