package com.example.controller;

import com.example.service.AccountDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Resource
    AccountDetailsService accountDetailsService;
    //获取验证码
    @GetMapping("/ask-code")
    public String code(@RequestParam(value = "email",required = true) String email){
        return accountDetailsService.getCode(email);
    }
    @PostMapping("/register")
    public String register(@RequestParam(value = "username",required = true) String username,
                           @RequestParam(value = "email",required = true)String eamil,
                           @RequestParam(value = "phone",required = true)String phone,
                           @RequestParam(value = "code",required = true)String code,
                           @RequestParam(value = "password",required = true)String password
    ){
        return accountDetailsService.registerAccountUser(username,eamil,code,password,phone);
    }

}
