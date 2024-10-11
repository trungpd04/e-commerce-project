package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.model.Order;
import com.nhom7.ecommercebackend.request.order.OrderDTO;
import com.nhom7.ecommercebackend.response.order.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface OrderService {
    Order createOrder(OrderDTO orderDTO);
    Order findOrderById(Long orderId);
    void deleteOrder(Long orderId); // just soft delete
    Page<OrderResponse> getAllOrder(String keyword, PageRequest pageRequest);
}
