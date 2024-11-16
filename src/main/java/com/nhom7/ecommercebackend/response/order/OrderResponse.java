package com.nhom7.ecommercebackend.response.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.model.Order;
import com.nhom7.ecommercebackend.model.OrderDetail;
import com.nhom7.ecommercebackend.model.Shipment;
import com.nhom7.ecommercebackend.response.payment.PaymentResponse;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private UUID id;

    @JsonProperty("user_place_order_id")
    private Long userId;

    @JsonProperty("receiver_full_name")
    private String receiverFullName;

    @JsonProperty("receiver_phone_number")
    private String receiverPhoneNumber;

    @JsonProperty("buyer_email")
    private String buyerEmail;


    @JsonProperty("note")
    private String note;

    @JsonProperty("order_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime orderDate;

    @JsonProperty("status")
    private String status;


    @JsonProperty("shipping_address")
    private String shippingAddress = "";

    @JsonProperty("shipping_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private LocalDate shippingDate;

    @JsonProperty("payment_details")
    private PaymentResponse paymentDetails;

    @JsonProperty("shipment")
    private Shipment shipment;

    @JsonProperty("order_details")
    private List<OrderDetailResponse> orderDetails;


    public static OrderResponse fromOrder(Order order) {
        List<OrderDetail> orderDetails = order.getOrderDetails();
        List<OrderDetailResponse> orderDetailResponses = orderDetails
                .stream()
                .map(OrderDetailResponse::fromOrderDetail).toList();
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .orderId(order.getId())
                .provider(order.getPayment().getProvider())
                .amount(order.getPayment().getAmount())
                .paid(order.getPayment().isPaid())
                .createdAt(order.getPayment().getCreatedAt())
                .updatedAt(order.getPayment().getUpdatedAt())
                .build();
        return OrderResponse
                .builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .receiverFullName(order.getReceiverFullName())
                .receiverPhoneNumber(order.getReceiverPhoneNumber())
                .buyerEmail(order.getBuyerEmail())
                .note(order.getNote())
                .orderDate(order.getOrderDate())
                .shippingDate(order.getShippingDate())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .paymentDetails(paymentResponse)
                .shipment(order.getShipment())
                .orderDetails(orderDetailResponses) //important
                .build();
    }
}