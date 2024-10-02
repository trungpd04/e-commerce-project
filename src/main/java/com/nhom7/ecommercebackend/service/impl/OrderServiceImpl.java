package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.*;
import com.nhom7.ecommercebackend.repository.OrderDetailRepository;
import com.nhom7.ecommercebackend.repository.OrderRepository;
import com.nhom7.ecommercebackend.repository.ProductRepository;
import com.nhom7.ecommercebackend.repository.UserRepository;
import com.nhom7.ecommercebackend.request.CartItemDTO;
import com.nhom7.ecommercebackend.request.OrderDTO;
import com.nhom7.ecommercebackend.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Order createOrder(OrderDTO orderDTO) {
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User does not exist!"));
        List<OrderDetail> orderDetails = new ArrayList<>();
        int numberOfProducts = 0;
        float totalMoney = 0;
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(map -> map.skip(Order::setId));
        Order newOrder = new Order();
        modelMapper.map(orderDTO, Order.class);
        newOrder.setUser(user);
        newOrder.setOrderDate(LocalDateTime.now());//lấy thời điểm hiện tại
        newOrder.setStatus(OrderStatus.PENDING);
        LocalDate shippingDate = orderDTO.getShippingDate() == null
                ? LocalDate.now() : orderDTO.getShippingDate();
        // nếu ngày ship trước hiện tại thông báo lỗi
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Date must be at least today !");
        }
        newOrder.setShippingDate(shippingDate);
        newOrder.setActive(true);
        newOrder.setTotalMoney(orderDTO.getTotalMoney());
        for(CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            Product existingProduct = productRepository.findProductById(cartItemDTO.getProductId())
                    .orElseThrow(() -> new DataNotFoundException("Product not exist!"));
            numberOfProducts += cartItemDTO.getQuantity();
            totalMoney += cartItemDTO.getQuantity() * existingProduct.getPrice();
            OrderDetail orderDetail = OrderDetail
                    .builder()
                    .order(newOrder)
                    .numberOfProducts(numberOfProducts)
                    .totalMoney(totalMoney)
                    .build();
            orderDetails.add(orderDetail);
        }
        newOrder.setOrderDetails(orderDetails);
        orderDetailRepository.saveAll(orderDetails);
        return orderRepository.save(newOrder);
    }

    @Override
    public Order findOrderByUserId(Long userId) {
        return null;
    }

    @Override
    public Order findOrderById(Long orderId) {
        return null;
    }

    @Override
    public void deleteOrder(Long orderId) {

    }

    @Override
    public List<Order> getAllOrder(String keyword, PageRequest pageRequest) {
        return null;
    }
}
