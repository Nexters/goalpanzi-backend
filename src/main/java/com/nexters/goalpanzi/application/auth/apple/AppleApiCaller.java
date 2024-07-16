package com.nexters.goalpanzi.application.auth.apple;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "appleApiCaller", url = "https://appleid.apple.com/auth")
public interface AppleApiCaller {

    @GetMapping("/keys")
    ApplePublicKeys getApplePublicKeys();
}
