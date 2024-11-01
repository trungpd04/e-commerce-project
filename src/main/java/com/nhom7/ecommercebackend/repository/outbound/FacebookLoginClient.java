package com.nhom7.ecommercebackend.repository.outbound;

import com.nhom7.ecommercebackend.request.login.ExchangeTokenRequest;
import com.nhom7.ecommercebackend.response.login.ExchangeTokenResponse;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "outbound-facebook-login", url = "https://graph.facebook.com/oauth/access_token")
public interface FacebookLoginClient {
    @GetMapping(value = "", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ExchangeTokenResponse exchangeToken(@QueryMap ExchangeTokenRequest request);
}
