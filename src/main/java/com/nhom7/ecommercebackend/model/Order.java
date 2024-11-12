package com.nhom7.ecommercebackend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_place_order_id")
    private User user;

    @Column(name = "receiver_full_name", length = 100)
    private String receiverFullName;

    @Column(name = "buyer_email", length = 100)
    private String buyerEmail;

    @Column(name = "receiver_phone_number",nullable = false, length = 100)
    @JsonProperty("receiver_phone_number")
    private String receiverPhoneNumber;

    @Column(name = "note", length = 100)
    private String note;

    @Column(name="order_date")
    private LocalDateTime orderDate;

    @Column(name = "status")
    private String status;


    @Column(name = "shipping_method")
    private String shippingMethod = "";

    @Column(name = "shipping_address")
    private String shippingAddress = "";

    @Column(name = "shipping_date")
    private LocalDate shippingDate;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "active")
    private Boolean active; //thuộc về admin

    @OneToOne(mappedBy = "order")
    private Payment payment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;

//    @ManyToOne
//    @JoinColumn(name = "coupon_id", nullable = true)
//    @JsonBackReference
//    private Coupon coupon = null;
}
