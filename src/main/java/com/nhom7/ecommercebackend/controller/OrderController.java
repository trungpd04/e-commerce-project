package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.Order;
import com.nhom7.ecommercebackend.request.order.OrderDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.response.order.OrderDetailResponse;
import com.nhom7.ecommercebackend.response.order.OrderListResponse;
import com.nhom7.ecommercebackend.response.order.OrderResponse;
import com.nhom7.ecommercebackend.service.OrderService;
import com.nhom7.ecommercebackend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static java.net.HttpURLConnection.HTTP_OK;

@RequiredArgsConstructor
@RequestMapping("${api.prefix}/orders")
@RestController
public class OrderController {

    private final OrderService orderService;
    @PostMapping("")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse createOrder(@RequestBody OrderDTO orderDTO) {
        Order newOrder = orderService.createOrder(orderDTO);
        return ApiResponse.builder()
                .message("Place order successfully!")
                .status(HTTP_OK)
                .data(OrderResponse.fromOrder(newOrder))
                .build();
    }
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse getOrderById(
            @PathVariable("orderId") Long orderId
            ) {
        Order existingOrder = orderService.findOrderById(orderId);
        return ApiResponse.builder()
                .message("Get order successfully!")
                .status(HTTP_OK)
                .data(OrderResponse.fromOrder(existingOrder))
                .build();
    }
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse getAllOrder(
            @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword,
            @RequestParam(value = "size", defaultValue = "5", required = false) int size,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page
    ) {
        PageRequest request = PageRequest.of(page, size);
        Page<OrderResponse> existingOrders = orderService.getAllOrder(keyword, request);
        OrderListResponse orderListResponse = OrderListResponse.builder()
                .orders(existingOrders.getContent())
                .totalPages(existingOrders.getTotalPages())
                .build();
        return ApiResponse.builder()
                .message("Get all order successfully!")
                .status(HTTP_OK)
                .data(orderListResponse)
                .build();
    }
    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse deactivateOrder(@PathVariable("orderId") Long orderId) {
        orderService.deleteOrder(orderId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Deactivate Order successfully!")
                .build();
    }
}
