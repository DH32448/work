package com.example.service;

import com.example.utils.JwtUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {
    
    @Resource
    private JwtUtils jwtUtils;
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String TOKEN_PREFIX = "token:";
    
    public String generateToken(String username) {
        String token = jwtUtils.generateToken(username);
        // 将token存储到Redis中，设置过期时间
        redisTemplate.opsForValue().set(TOKEN_PREFIX + username, token, jwtUtils.extractExpiration(token).getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        return token;
    }
    
    public boolean validateToken(String token, String username) {
        // 从Redis中获取token
        String storedToken = (String) redisTemplate.opsForValue().get(TOKEN_PREFIX + username);
        if (storedToken == null || !storedToken.equals(token)) {
            return false;
        }
        return jwtUtils.validateToken(token, username);
    }
    
    public void invalidateToken(String username) {
        redisTemplate.delete(TOKEN_PREFIX + username);
    }
} 