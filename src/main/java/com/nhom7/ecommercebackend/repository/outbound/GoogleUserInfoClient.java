package com.nhom7.ecommercebackend.repository.outbound;

import com.nhom7.ecommercebackend.response.login.UserOauth2Info;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "outbound-google-user-info", url = "https://www.googleapis.com")
public interface GoogleUserInfoClient {
    @GetMapping(value = "/oauth2/v1/userinfo")
    UserOauth2Info userInfo(@RequestParam(value = "alt", defaultValue = "json") String alt,
                            @RequestParam("access_token") String accessToken);

}
