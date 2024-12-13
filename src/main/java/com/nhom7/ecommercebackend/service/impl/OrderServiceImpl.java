package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.*;
import com.nhom7.ecommercebackend.model.*;
import com.nhom7.ecommercebackend.repository.*;
import com.nhom7.ecommercebackend.request.cart.CartItemDTO;
import com.nhom7.ecommercebackend.request.order.OrderDTO;
import com.nhom7.ecommercebackend.response.order.OrderResponse;
import com.nhom7.ecommercebackend.service.OrderService;
import com.nhom7.ecommercebackend.utils.SecurityUtils;
import com.nhom7.ecommercebackend.validation.EnumConstraint;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final SecurityUtils userUtil;
    private final PaymentRepository paymentRepository;
    private final ShipmentRepository shipmentRepository;

    @Override
    @Transactional
    public Order createOrder(OrderDTO orderDTO, String paymentType)
            throws UnsupportedPaymentException, UnsupportedShipmentException {
       return placeOrder(orderDTO, paymentType);
    }

    private Order placeOrder(OrderDTO orderDTO, String paymentType) throws UnsupportedPaymentException, UnsupportedShipmentException {
        Order newOrder = new Order();
        User user = userUtil.getLoggedInUser();
        modelMapper.map(orderDTO, newOrder);
        newOrder.setBuyerEmail(user.getEmail());
        newOrder.setOrderDate(LocalDateTime.now());//lấy thời điểm hiện tại
        newOrder.setStatus(OrderStatus.PENDING);


        List<OrderDetail> orderDetails = new ArrayList<>();

        for(CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            Product existingProduct = productRepository.findProductById(cartItemDTO.getProductId())
                    .orElseThrow(() -> new DataNotFoundException(MessageKeys.PRODUCT_NOT_FOUND.getMessage()));
            OrderDetail orderDetail = OrderDetail.builder()
                    .order(newOrder)
                    .product(existingProduct)
                    .quantity(cartItemDTO.getQuantity())
                    .price(existingProduct.getPrice())
                    .build();
            orderDetails.add(orderDetail);
        }

        newOrder.setOrderDetails(orderDetails);

        // nếu ngày ship trước hiện tại thông báo lỗi
        Shipment existingShipment = shipmentRepository.findById(orderDTO.getShipmentId())
                .orElseThrow(() -> new DataNotFoundException("Shipment method not supported"));
        if(!existingShipment.isActive()) {
            throw new UnsupportedShipmentException("Shipment method not supported");
        }

        BigDecimal totalMoney = orderDetails
                .stream()
                .map(orderDetail -> orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(existingShipment.getPrice());

        newOrder.setActive(true);
        newOrder.setUser(user);
        newOrder.setShipment(existingShipment);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setStatus(OrderStatus.PENDING);

        LocalDate estimatedShippingDate =
                LocalDate.now().plusDays(existingShipment.getEstimatedDay());
        newOrder.setShippingDate(estimatedShippingDate);

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
            case "vnpay":
                Payment vnPayPayment = Payment.builder()
                        .amount(totalMoney)
                        .provider(PaymentProvider.vnpay)
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
            case "vnpay":
                if(status.equalsIgnoreCase("00")
                        && payment.getProvider().equals(PaymentProvider.vnpay)) {
                    payment.setPaid(true);
                    paymentRepository.save(payment);
                }
        }

        return orderRepository.save(existingOrder);
    }

    @Override
    public Order updateOrderStatus(
            UUID orderId,
            String status
    ) throws InvalidParamException {
        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() -> new DataNotFoundException(MessageKeys.ORDER_NOT_FOUND.toString()));
        if(!status.trim().equalsIgnoreCase("cancelled")) {
            if(order.getPayment().isPaid()) {
                order.setStatus(status);
            }else{
                throw new InvalidParamException("Unpaid Order");
            }
        }else{
            if(status.trim().equalsIgnoreCase("cancelled")) {
                order.setStatus("cancelled");
            }else{
                throw new InvalidParamException("Status not supported");
            }
        }
        return orderRepository.save(order);
    }
}
