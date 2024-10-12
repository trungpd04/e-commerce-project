package com.nhom7.ecommercebackend.repository.outbound;

import com.nhom7.ecommercebackend.model.UserOauth2Info;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "outbound-user-info", url = "https://www.googleapis.com")
public interface OutboundUserInfoClient {
    @GetMapping(value = "/oauth2/v1/userinfo")
    UserOauth2Info userInfo(@RequestParam("alt") String alt,
                            @RequestParam("access_token") String accessToken);

}
