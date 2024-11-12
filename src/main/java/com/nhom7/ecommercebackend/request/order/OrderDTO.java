package com.nhom7.ecommercebackend.request.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.request.cart.CartItemDTO;
import com.nhom7.ecommercebackend.request.payment.PaymentDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
@Builder
public class OrderDTO {


    @JsonProperty("receiver_full_name")
    private String receiverFullName;


    @JsonProperty("receiver_phone_number")
    @NotBlank(message = "Phone number is required")
    @Size(min = 5, message = "Phone number must be at least 5 characters")
    private String receiverPhoneNumber;

    @JsonProperty("status")
    private String status;

    private String note;


    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;


//    @JsonProperty("coupon_code")
//    private String couponCode;

    @JsonProperty("cart_items")
    private List<CartItemDTO> cartItems;

    @JsonProperty("payment_details")
    private PaymentDTO paymentDetails;
}
