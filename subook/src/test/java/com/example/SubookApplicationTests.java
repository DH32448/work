package com.example;

import com.example.entity.dto.AccountDetails;
import com.example.mapper.AccountDetailsMapper;
import com.example.service.AccountDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.xml.crypto.Data;
import java.util.UUID;

@SpringBootTest
class SubookApplicationTests {
    @Autowired
    AccountDetailsMapper detailsMapper;
    @Autowired
    PasswordEncoder encoder;

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
    }

}
