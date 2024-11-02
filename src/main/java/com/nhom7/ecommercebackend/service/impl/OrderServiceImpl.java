package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.exception.MessageKeys;
import com.nhom7.ecommercebackend.model.*;
import com.nhom7.ecommercebackend.repository.OrderRepository;
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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final SecurityUtils userUtil;


    @Override
    @Transactional
    public Order createOrder(OrderDTO orderDTO) {
//        User loggedInUser = userUtil.getLoggedInUser();
//        Cart cart = cartRepository.findByUserId(loggedInUser.getId())
//                .orElseThrow(() -> new DataNotFoundException("Empty Cart"));
        Order newOrder = placeOrder(orderDTO);
//        List<OrderDetail> orderDetails = createOrderDetails(newOrder, cart);
//        newOrder.setTotalMoney(calculateTotalAmount(orderDetails));
//        newOrder.setOrderDetails(orderDetails);
        return orderRepository.save(newOrder);
    }
    
//    private List<OrderDetail> createOrderDetails(Order order, Cart cart) {
//        return cart.getCartItems().stream().map(cartItem -> {
//            Product product = cartItem.getProduct();
//            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
//            BigDecimal totalMoneyPerCartItem = product.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
//            productRepository.save(product);
//            return OrderDetail.builder()
//                    .numberOfProducts(cartItem.getQuantity())
//                    .totalMoney(totalMoneyPerCartItem)
//                    .order(order)
//                    .product(product)
//                    .build();
//        }).collect(Collectors.toList());
//    }
    private Order placeOrder(OrderDTO orderDTO) {
        Order newOrder = new Order();
        User user = userUtil.getLoggedInUser();
        modelMapper.map(orderDTO, newOrder);
//        newOrder.setUser(cart.getUser());
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
        newOrder.setTotalMoney(totalMoney);
        newOrder.setShippingDate(shippingDate);
        newOrder.setActive(true);
        newOrder.setUser(user);
        return newOrder;
    }
//    private BigDecimal calculateTotalAmount(List<OrderDetail> orderItemList) {
//        return orderItemList
//                .stream()
//                .map(item -> item.getProduct().getPrice()
//                        .multiply(new BigDecimal(item.getNumberOfProducts())))
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
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

    @Override
    public Page<OrderResponse> getMyOrders(String keyword, PageRequest pageRequest) {
        Page<Order> orderResponsePage = orderRepository.findAll(keyword, pageRequest);
        return orderResponsePage.map(OrderResponse::fromOrder);
    }
}
