package com.nhom7.ecommercebackend.repository.outbound;

import com.nhom7.ecommercebackend.response.login.UserOauth2Info;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "outbound-facebook-user-info", url = "https://graph.facebook.com/me?fields=id,name,email,picture.type(large)")
public interface FacebookUserInfoClient {
    @GetMapping("")
    UserOauth2Info userInfo(@RequestParam("access_token") String accessToken);
}
