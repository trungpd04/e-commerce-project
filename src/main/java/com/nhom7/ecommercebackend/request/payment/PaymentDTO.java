package com.nhom7.ecommercebackend.request.payment;

import com.nhom7.ecommercebackend.model.PaymentProvider;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentDTO {

    @Min(value = 0, message = "Amount must be >= 0")
    private BigDecimal amount;

    private PaymentProvider provider;

}
