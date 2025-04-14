package com.example.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.AccountDetails;
import com.example.mapper.AccountDetailsMapper;
import com.example.service.AccountDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountDetailsServiceImpl extends ServiceImpl<AccountDetailsMapper, AccountDetails> implements AccountDetailsService {


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountDetails account = this.findAccountByPhoneOrEmail(username);
        System.out.println(account);
        if(account == null)
            throw new UsernameNotFoundException("用户名或密码错误");
        return User
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }


    public AccountDetails findAccountByPhoneOrEmail(String text) {
        return this.query()
                .eq("phone", text).or()
                .eq("email", text)
                .one();
    }
}
