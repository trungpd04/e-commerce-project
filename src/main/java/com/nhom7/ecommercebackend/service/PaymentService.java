package com.nhom7.ecommercebackend.service;

import java.io.UnsupportedEncodingException;

public interface PaymentService {
    String createVNPayURL(String orderId,
                          Long amount
                          ) throws UnsupportedEncodingException;
}
