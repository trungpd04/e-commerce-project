package com.nhom7.ecommercebackend.response.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Builder
@Setter
public class VNPayResponse {

    @JsonProperty("vnp_Amount")
    private Long vnpAmount;

    @JsonProperty("vnp_BankCode")
    private String vnpBankCode;

    @JsonProperty("vnp_CartType")
    private String vnpCartType;

    @JsonProperty("vnp_OrderInfo")
    private String vnpOrderInfo;

    @JsonProperty("vnp_PayDate")
    private Date vnpPayDate;

    @JsonProperty("vnp_ResponseCode")
    private int vnpResponseCode;

    @JsonProperty("vnp_TransactionStatus")
    private int vnpTransactionStatus;
}
