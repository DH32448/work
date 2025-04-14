package com.example.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.AccountDetails;
import com.example.mapper.AccountDetailsMapper;
import com.example.service.AccountDetailsService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Random;

@Service
public class AccountDetailsServiceImpl extends ServiceImpl<AccountDetailsMapper, AccountDetails> implements AccountDetailsService {
    @Resource
    JavaMailSender sender;

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
    //注册
    //发送验证码

    public String getCode(HttpSession session, @RequestParam(value = "email",required = true) String email){
        System.out.println("验证码");
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        session.setAttribute("code",code);
        session.setAttribute("email",email);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("你的验证码是：");
        message.setText("有效时间3分钟："+code);
        message.setFrom("x1815097512@163.com");
        message.setTo(email);
        sender.send(message);
        return "发送成功";
    }

}
