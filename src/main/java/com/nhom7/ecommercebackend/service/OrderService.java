package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.model.Order;
import com.nhom7.ecommercebackend.repository.OrderDetailRepository;
import com.nhom7.ecommercebackend.request.OrderDTO;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface OrderService {
    Order createOrder(OrderDTO orderDTO);
    Order findOrderByUserId(Long userId);
    Order findOrderById(Long orderId);
    void deleteOrder(Long orderId); // just soft delete
    List<Order> getAllOrder(String keyword, PageRequest pageRequest);
}
