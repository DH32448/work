package com.example;

import com.example.entity.AccountInfo;
import com.example.entity.dto.AccountDetails;
import com.example.entity.ot.SexEnum;
import com.example.mapper.AccountDetailsMapper;
import com.example.mapper.AccountInfoMapper;
import com.example.service.AccountDetailsService;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@SpringBootTest
class SubookApplicationTests {
    @Autowired
    AccountDetailsMapper detailsMapper;
    @Autowired
    PasswordEncoder encoder;
    @Resource
    JavaMailSender sender;
    @Autowired
    AccountInfoMapper infoMapper;

    @Test
    void contextLoads() {
        AccountDetails details = new AccountDetails();
        details.setUsername(String.valueOf(UUID.randomUUID()));
        details.setEmail("test@163.com");
        details.setRole("USER");
        details.setPhone("123456");
        details.setPassword(encoder.encode("123456"));
        int insert = detailsMapper.insert(details);
        System.out.println(insert);

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAid(details.getId());
        accountInfo.setAge(18);
        accountInfo.setName("大佬");
        accountInfo.setRegisterTime(LocalDateTime.now());
        accountInfo.setSex(SexEnum.MALE);
        accountInfo.setText("我才18岁");
        int insertinfo = infoMapper.insert(accountInfo);
        System.out.println(insertinfo);
    }
    @Test
    void mail(){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("恭喜你！中大奖了");
        message.setText("你中尼玛！！！");
        message.setTo("dh32448@163.com");
        message.setFrom("x1815097512@163.com");
        sender.send(message);
    }

}
