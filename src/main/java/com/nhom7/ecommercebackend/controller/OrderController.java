package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.Order;
import com.nhom7.ecommercebackend.request.OrderDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.net.HttpURLConnection.HTTP_OK;

@RequiredArgsConstructor
@RequestMapping("${api.prefix}/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("")
    public ApiResponse createOrder(@RequestBody OrderDTO orderDTO) {
        Order newOrder = orderService.createOrder(orderDTO);
        return ApiResponse.builder()
                .message("Place order successfully!")
                .status(HTTP_OK)
                .data(newOrder)
                .build();
    }
}
