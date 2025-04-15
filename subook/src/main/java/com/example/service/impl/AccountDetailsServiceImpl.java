package com.example.service.impl;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.AccountDetails;
import com.example.entity.vo.RestBean;
import com.example.mapper.AccountDetailsMapper;
import com.example.service.AccountDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountDetailsServiceImpl extends ServiceImpl<AccountDetailsMapper, AccountDetails> implements AccountDetailsService {
    @Resource
    JavaMailSender sender;
    @Resource
    AccountDetailsMapper detailsMapper;
    @Resource
    PasswordEncoder encoder;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
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

    public String getCode(String email){
        System.out.println("验证码");
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("你的验证码是：");
        message.setText("有效时间3分钟："+code);
        message.setFrom("x1815097512@163.com");
//        if (StringUtils.equals(email, "%@%.com")) {
            redisTemplate.opsForValue().set(email, code, 3, TimeUnit.MINUTES);
            message.setTo(email);
            sender.send(message);
            System.out.println("验证码发送成功");
            return RestBean.success().asJsonString();
//        }else return RestBean.failure(400,"邮箱格式不正确").asJsonString();
    }

    @Override
    public String registerAccountUser(String username, String email, String code, String password, String phone) {
        return this.registerAccount(username,email,code,password,phone,"USER");
    }

    public String registerAccount(String username, String email, String code, String password, String phone, String role) {
        // 根据邮箱（key）获取验证码
        Object storedCodeObj = redisTemplate.opsForValue().get(email);
        if (storedCodeObj != null) {
            // 将存储的验证码转换为String类型
            String storedCode = String.valueOf(storedCodeObj);
            if (storedCode.equals(code)) {
                AccountDetails details = new AccountDetails();
                details.setUsername(username);
                details.setPassword(encoder.encode(password));
                details.setEmail(email);
                details.setRole(role);
                details.setPhone(phone);
                // 验证码正确，执行业务逻辑
                int insert = detailsMapper.insert(details);
                if (insert > 0) {
                    redisTemplate.delete(email);
                    return RestBean.success("注册成功").asJsonString();
                } else {
                    return RestBean.failure(400,"注册失败").asJsonString();
                }
            }
        }
        return RestBean.failure(400,"验证码错误").asJsonString();
    }
}
