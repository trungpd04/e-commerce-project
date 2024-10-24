package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.configuration.payment.VNPay.VNPayConfig;
import com.nhom7.ecommercebackend.configuration.payment.VNPay.VNPayUtil;
import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.repository.OrderRepository;
import com.nhom7.ecommercebackend.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final HttpServletRequest request;
    private final OrderRepository orderRepository;
    private final VNPayConfig vnPayConfig;
    private final VNPayUtil vnPayUtil;
    @Override
    public String createVNPayURL(String orderId,
                                 Long amount) throws UnsupportedEncodingException {
        Map<String, String> vnPayParams = this.vnPayConfig.getVNPayConfig();
//        vnPayParams.put("vnp_BankCode", bankCode);
        if(!orderRepository.existsById(UUID.fromString(orderId))) {
            throw new DataNotFoundException("Order does not exist!");
        }else{
            vnPayParams.put("vnp_TxnRef", orderId);
        }
        String paymentInfo = STR."Thanh toan cho don hang: \{orderId}";
        vnPayParams.put("vnp_OrderInfo", paymentInfo);
        Long paymentAmount = amount * 100L;
        vnPayParams.put("vnp_Amount", String.valueOf(paymentAmount));
        vnPayParams.put("vnp_IpAddr", vnPayUtil.getIpAddress(request));
        List fieldNames = new ArrayList(vnPayParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnPayParams.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(fieldValue);
                //Build query
                query.append(URLEncoder.encode(fieldName,
                        StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue,
                        StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
//        System.out.println(query.toString());
        String secureHash = vnPayUtil.hmacSHA512WithHashSecret(query.toString());
//        System.out.println(secureHash);
        queryUrl += STR."&vnp_SecureHash=\{secureHash}";
        return STR."\{vnPayConfig.getVnPayUrl()}?\{queryUrl}";
    }
}
