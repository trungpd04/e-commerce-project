package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.exception.UnsupportedPaymentException;
import com.nhom7.ecommercebackend.model.Order;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.request.order.OrderDTO;
import com.nhom7.ecommercebackend.response.order.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    Order createOrder(OrderDTO orderDTO, String paymentType) throws UnsupportedPaymentException;
    Order findOrderById(UUID orderId);
    void deleteOrder(UUID orderId); // just soft delete
    Page<OrderResponse> getAllOrder(String keyword, String status, PageRequest pageRequest);
    Page<OrderResponse> getMyOrders(User user, String status, PageRequest pageRequest);
    Order updatePaymentStatus(UUID orderId, String paymentType, String status);
}
