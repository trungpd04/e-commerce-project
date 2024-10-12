package com.nhom7.ecommercebackend.repository.outbound;

import com.nhom7.ecommercebackend.model.UserOauth2Info;
import com.nhom7.ecommercebackend.request.login.ExchangeTokenRequest;
import com.nhom7.ecommercebackend.response.login.ExchangeTokenResponse;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.*;

@FeignClient(name = "outbound-login", url = "https://oauth2.googleapis.com")
public interface OutboundLoginClient {
    @PostMapping(value = "/token", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ExchangeTokenResponse exchangeToken(@QueryMap ExchangeTokenRequest request);
}
