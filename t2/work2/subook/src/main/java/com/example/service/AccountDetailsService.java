package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.AccountDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountDetailsService extends IService<AccountDetails>, UserDetailsService {
    String getCode(String email);
    String registerAccountUser(String username,String email,String code,String password);

}
