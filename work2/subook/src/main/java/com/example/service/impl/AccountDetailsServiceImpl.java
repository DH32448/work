package com.example.service.impl;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.AccountInfo;
import com.example.entity.dto.AccountDetails;
import com.example.entity.ot.SexEnum;
import com.example.entity.vo.RestBean;
import com.example.mapper.AccountDetailsMapper;
import com.example.mapper.AccountInfoMapper;
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
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
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
    @Resource
    AccountInfoMapper infoMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            AccountDetails accountDetails = detailsMapper.selectOne(
                    this.query().select("id", "email", "username", "password", "role")
                            .eq("email", username).getWrapper());
            System.out.println(accountDetails);
            if (accountDetails == null)
                throw new UsernameNotFoundException("用户名或邮箱不存在");
            //id,username,password,accountNonExpired,accountNonLocked,credentialsNonLocked,enabled
            return User.builder()
                    .username(accountDetails.getId().toString())
                    .password(accountDetails.getPassword())
                    .roles(accountDetails.getRole()).build();
        } catch (Exception e) {
            throw new UsernameNotFoundException("查询错误，请查看邮箱是否合法");
        }
    }

    public AccountDetails findAccountByEmail(String email) {
        AccountDetails accountDetails = detailsMapper.selectOne(
                this.query().select("id", "email", "username", "password", "role")
                        .eq("email", email).getWrapper());
        System.out.println(accountDetails);
        return accountDetails;
    }

    public String getCode(String email){
        Random random = new Random();
        int randomNumber = 10000 + random.nextInt(90000);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("【宿书】您的验证码");
        message.setText("尊敬的用户您好！\n\n您的验证码为: "+randomNumber+"\n\n有效时间为10分钟；\n\n如果不是您本人操作，请忽略此邮件。");
        //收件人
        message.setTo(email);
        //发件人
        message.setFrom("x1815097512@163.com");

        sender.send(message);
        redisTemplate.opsForValue().set(email, String.valueOf(randomNumber), 10, TimeUnit.MINUTES);
        return RestBean.success().asJsonString();
    }

    @Override
    public String registerAccountUser(String username, String email, String code, String password) {
        return this.registerAccount(username, email, code, password, "USER");
    }

    public String registerAccount(String username, String email, String code, String password, String role) {
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
                // 验证码正确，执行业务逻辑
                int insert = detailsMapper.insert(details);
                if (insert > 0) {
                    redisTemplate.delete(email);
                    //初始化info
                    AccountInfo accountInfo = new AccountInfo();
                    accountInfo.setAid(details.getId());
                    accountInfo.setName(String.valueOf(details.getUsername()));
                    accountInfo.setRegisterTime(LocalDateTime.now());
                    // 设置默认性别为"保密"
                    accountInfo.setSex(SexEnum.SECRET);
                    infoMapper.insert(accountInfo);
                    return RestBean.success("注册成功").asJsonString();
                } else {
                    return RestBean.failure(400,"注册失败").asJsonString();
                }
            }
        }
        return RestBean.failure(400,"验证码错误").asJsonString();
    }
}
