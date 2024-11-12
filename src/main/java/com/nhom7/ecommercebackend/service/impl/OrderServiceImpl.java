package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.exception.MessageKeys;
import com.nhom7.ecommercebackend.exception.UnsupportedPaymentException;
import com.nhom7.ecommercebackend.model.*;
import com.nhom7.ecommercebackend.repository.OrderRepository;
import com.nhom7.ecommercebackend.repository.PaymentRepository;
import com.nhom7.ecommercebackend.repository.ProductRepository;
import com.nhom7.ecommercebackend.request.cart.CartItemDTO;
import com.nhom7.ecommercebackend.request.order.OrderDTO;
import com.nhom7.ecommercebackend.response.order.OrderResponse;
import com.nhom7.ecommercebackend.service.OrderService;
import com.nhom7.ecommercebackend.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final SecurityUtils userUtil;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public Order createOrder(OrderDTO orderDTO, String paymentType) throws UnsupportedPaymentException {
       return placeOrder(orderDTO, paymentType);
    }

    private Order placeOrder(OrderDTO orderDTO, String paymentType) throws UnsupportedPaymentException {
        Order newOrder = new Order();
        User user = userUtil.getLoggedInUser();
        modelMapper.map(orderDTO, newOrder);
        newOrder.setBuyerEmail(user.getEmail());
        newOrder.setOrderDate(LocalDateTime.now());//lấy thời điểm hiện tại
        newOrder.setStatus(OrderStatus.PENDING);
        LocalDate shippingDate = orderDTO.getShippingDate() == null
                ? LocalDate.now() : orderDTO.getShippingDate();

        List<OrderDetail> orderDetails = new ArrayList<>();

        for(CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            Product existingProduct = productRepository.findProductById(cartItemDTO.getProductId())
                    .orElseThrow(() -> new DataNotFoundException(MessageKeys.PRODUCT_NOT_FOUND.getMessage()));
            BigDecimal totalMany = existingProduct.getPrice().multiply(new BigDecimal(cartItemDTO.getQuantity()));
            OrderDetail orderDetail = OrderDetail.builder()
                    .order(newOrder)
                    .product(existingProduct)
                    .numberOfProducts(cartItemDTO.getQuantity())
                    .totalMoney(totalMany)
                    .build();
            orderDetails.add(orderDetail);
        }

        newOrder.setOrderDetails(orderDetails);

        // nếu ngày ship trước hiện tại thông báo lỗi
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Date must be at least today !");
        }
        BigDecimal totalMoney = orderDetails.stream().map(OrderDetail::getTotalMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        newOrder.setShippingDate(shippingDate);
        newOrder.setActive(true);
        newOrder.setUser(user);

        switch (paymentType.trim().toLowerCase()) {
            case "cash":
                Payment caspPayment = Payment.builder()
                        .amount(totalMoney)
                        .provider(PaymentProvider.cash)
                        .build();
                caspPayment.setOrder(newOrder);
                paymentRepository.save(caspPayment);
                newOrder.setPayment(caspPayment);
                break;
            case "vn_pay":
                Payment vnPayPayment = Payment.builder()
                        .amount(totalMoney)
                        .provider(PaymentProvider.vn_pay)
                        .build();
                vnPayPayment.setOrder(newOrder);
                paymentRepository.save(vnPayPayment);
                newOrder.setPayment(vnPayPayment);
                break;
                default:
                    throw new UnsupportedPaymentException("Unsupported payment! ");
        }
        orderRepository.save(newOrder);
        return newOrder;
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
    public Page<OrderResponse> getAllOrder(String keyword, String status, PageRequest pageRequest) {
        Page<Order> orderResponsePage = orderRepository.findAll(keyword, status, pageRequest);
        return orderResponsePage.map(OrderResponse::fromOrder);
    }

    @Override
    public Page<OrderResponse> getMyOrders(User user, String status, PageRequest pageRequest) {
        Page<Order> orderResponsePage = orderRepository.findAllByStatusAndUser(status,user, pageRequest);
        return orderResponsePage.map(OrderResponse::fromOrder);
    }

    @Override
    public Order updatePaymentStatus(UUID orderId, String paymentType, String status) {
        Order existingOrder = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("Order not found!"));
        Payment payment = existingOrder.getPayment();
        switch (paymentType.trim().toLowerCase()) {
            case "cash":
                if(payment.getProvider().equals(PaymentProvider.cash)) {
                    payment.setPaid(true);
                    paymentRepository.save(payment);
                }
                break;
            case "vn_pay":
                if(status.equalsIgnoreCase("00")
                        && payment.getProvider().equals(PaymentProvider.vn_pay)) {
                    payment.setPaid(true);
                    paymentRepository.save(payment);
                }
        }

        return orderRepository.save(existingOrder);
    }

}
