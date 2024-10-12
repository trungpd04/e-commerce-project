package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.*;
import com.nhom7.ecommercebackend.repository.OrderDetailRepository;
import com.nhom7.ecommercebackend.repository.OrderRepository;
import com.nhom7.ecommercebackend.repository.ProductRepository;
import com.nhom7.ecommercebackend.repository.UserRepository;
import com.nhom7.ecommercebackend.request.cart.CartItemDTO;
import com.nhom7.ecommercebackend.request.order.OrderDTO;
import com.nhom7.ecommercebackend.response.order.OrderResponse;
import com.nhom7.ecommercebackend.service.OrderService;
import com.nhom7.ecommercebackend.exception.MessageKeys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
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
        User user = userRepository.findById(orderDTO.getUserPlaceOrderId())
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_EXIST.toString()));
        List<OrderDetail> orderDetails = new ArrayList<>();
        Order newOrder = new Order();
        modelMapper.map(orderDTO, newOrder);
        newOrder.setUser(user);
        newOrder.setBuyerEmail(user.getEmail());
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
        float orderTotalMoney = 0;
        for(CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            int numberOfProducts = 0;
            float totalMoneyOfProduct = 0;
            Product existingProduct = productRepository.findProductById(cartItemDTO.getProductId())
                    .orElseThrow(() -> new DataNotFoundException("Product not exist!"));
            numberOfProducts += cartItemDTO.getQuantity();
            totalMoneyOfProduct += cartItemDTO.getQuantity() * existingProduct.getPrice();
            OrderDetail orderDetail = OrderDetail
                    .builder()
                    .order(newOrder)
                    .product(existingProduct)
                    .numberOfProducts(numberOfProducts)
                    .totalMoney(totalMoneyOfProduct)
                    .build();
            orderTotalMoney += orderTotalMoney + orderDetail.getTotalMoney();
            orderDetails.add(orderDetail);
        }
        newOrder.setTotalMoney(orderTotalMoney);
        newOrder.setOrderDetails(orderDetails);
        orderDetailRepository.saveAll(orderDetails);
        return orderRepository.save(newOrder);
    }


    @Override
    public Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.ORDER_NOT_FOUND.toString()));
    }

    @Override
    public void deleteOrder(Long orderId) {
        Order existingOrder = findOrderById(orderId);
        existingOrder.setActive(false);
        orderRepository.save(existingOrder);
    }

    @Override
    public Page<OrderResponse> getAllOrder(String keyword, PageRequest pageRequest) {
        Page<Order> orderResponsePage = orderRepository.findAll(keyword, pageRequest);
        return orderResponsePage.map(OrderResponse::fromOrder);
    }
}
