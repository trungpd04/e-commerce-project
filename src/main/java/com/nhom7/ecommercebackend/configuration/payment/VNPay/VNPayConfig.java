package com.nhom7.ecommercebackend.configuration.payment.VNPay;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
@Configuration
public class VNPayConfig {

    @Getter
    @Value("${payment.vn_pay.vnp_url}")
    private String vnPayUrl;
    @Value("${payment.vn_pay.vnp_version}")
    private String vnpVersion;
    @Value("${payment.vn_pay.vnp_command}")
    private String vnpCommand;
    @Value("${payment.vn_pay.vnp_tmn_code}")
    private String vnpTmnCode;
    // private String vnpBankCode; put bên ngoài vào map
    @Value("${payment.vn_pay.vnp_locale}")
    private String vnpLocale;
    @Value("${payment.vn_pay.vnp_curr_code}")
    private String vnpCurrCode; // default: VND
    // private String vnpTxnRef; // order id // truyền vào hàm put bên ngoài vào map
    // private String vnpOrderInfo; // nội dung thanh toán // truyền vào hàm put bên ngoài vào map
    @Value("${payment.vn_pay.vnp_order_type}")
    private String vnpOrderType;
    // private Long vnpAmount; // put bên ngoài vào map
    @Value("${payment.vn_pay.vnp_return_url}")
    private String vnpReturnUrl;
    // private String vnpIpAddress; // put ben ngoai vao
    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnPayConfig = new HashMap<>();
        vnPayConfig.put("vnp_Version", this.vnpVersion);
        vnPayConfig.put("vnp_Command", this.vnpCommand);
        vnPayConfig.put("vnp_TmnCode", this.vnpTmnCode);
        vnPayConfig.put("vnp_CurrCode", this.vnpCurrCode);
        vnPayConfig.put("vnp_OrderType", this.vnpOrderType);
        vnPayConfig.put("vnp_Locale", this.vnpLocale);
        vnPayConfig.put("vnp_ReturnUrl", this.vnpReturnUrl);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        vnPayConfig.put("vnp_CreateDate", vnpCreateDate);
        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(calendar.getTime());
        vnPayConfig.put("vnp_ExpireDate", vnp_ExpireDate);
        return vnPayConfig;
    }
}
