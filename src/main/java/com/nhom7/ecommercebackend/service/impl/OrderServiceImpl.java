package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.exception.MessageKeys;
import com.nhom7.ecommercebackend.model.*;
import com.nhom7.ecommercebackend.repository.CartRepository;
import com.nhom7.ecommercebackend.repository.OrderRepository;
import com.nhom7.ecommercebackend.repository.ProductRepository;
import com.nhom7.ecommercebackend.request.order.OrderDTO;
import com.nhom7.ecommercebackend.response.order.OrderResponse;
import com.nhom7.ecommercebackend.service.OrderService;
import com.nhom7.ecommercebackend.utils.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final UserUtil userUtil;

    @Override
    @Transactional
    public Order createOrder(OrderDTO orderDTO) {
        User loggedInUser = userUtil.getLoggedInUser();
        Cart cart = cartRepository.findByUserId(loggedInUser.getId())
                .orElseThrow(() -> new DataNotFoundException("Empty Cart"));
        Order newOrder = createOrder(cart, orderDTO);
        List<OrderDetail> orderDetails = createOrderDetails(newOrder, cart);
        newOrder.setTotalMoney(calculateTotalAmount(orderDetails));
        newOrder.setOrderDetails(orderDetails);
        return orderRepository.save(newOrder);
    }
    
    private List<OrderDetail> createOrderDetails(Order order, Cart cart) {
        return cart.getCartItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            BigDecimal totalMoneyPerCartItem = product.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
            productRepository.save(product);
            return OrderDetail.builder()
                    .numberOfProducts(cartItem.getQuantity())
                    .totalMoney(totalMoneyPerCartItem)
                    .order(order)
                    .product(product)
                    .build();
        }).collect(Collectors.toList());
    }
    private Order createOrder(Cart cart, OrderDTO orderDTO) {
        Order newOrder = new Order();
        modelMapper.map(orderDTO, newOrder);
        newOrder.setUser(cart.getUser());
        newOrder.setBuyerEmail(cart.getUser().getEmail());
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
        newOrder.setUser(cart.getUser());
        return newOrder;
    }
    private BigDecimal calculateTotalAmount(List<OrderDetail> orderItemList) {
        return orderItemList
                .stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(new BigDecimal(item.getNumberOfProducts())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    @Override
    public Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.ORDER_NOT_FOUND.toString()));
    }

    @Override
    public void deleteOrder(UUID orderId) {
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
