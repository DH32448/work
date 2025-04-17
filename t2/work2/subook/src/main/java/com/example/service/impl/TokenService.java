package com.example.service.impl;

import com.example.utils.JwtUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TokenService {
    
    @Resource
    private JwtUtils jwtUtils;
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String TOKEN_PREFIX = "token:";
    private static final String BLACKLIST_PREFIX = "blacklist:";
    
    public String generateToken(String username) {
        String token = jwtUtils.generateToken(username);
        // 将token存储到Redis中，设置过期时间
        redisTemplate.opsForValue().set(TOKEN_PREFIX + username, token, jwtUtils.extractExpiration(token).getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        log.info("Token generated for user: {}", username);
        return token;
    }
    
    /**
     * 为用户ID生成令牌
     * @param userId 用户ID
     * @return 生成的JWT令牌
     */
    public String generateTokenForUserId(Integer userId) {
        String token = jwtUtils.generateTokenForUserId(userId);
        // 将token存储到Redis中，设置过期时间
        String userIdStr = userId.toString();
        redisTemplate.opsForValue().set(TOKEN_PREFIX + userIdStr, token, 
            jwtUtils.extractExpiration(token).getTime() - System.currentTimeMillis(), 
            TimeUnit.MILLISECONDS);
        log.info("Token generated for userId: {}", userId);
        return token;
    }
    
    public boolean validateToken(String token, String username) {
        // 检查token是否在黑名单中
        if (isTokenBlacklisted(token)) {
            log.warn("Token is blacklisted for user: {}", username);
            return false;
        }
        
        // 从Redis中获取token
        String storedToken = (String) redisTemplate.opsForValue().get(TOKEN_PREFIX + username);
        if (storedToken == null || !storedToken.equals(token)) {
            log.warn("Token validation failed for user: {}", username);
            return false;
        }
        return jwtUtils.validateToken(token, username);
    }
    
    public void invalidateToken(String username) {
        String tokenKey = TOKEN_PREFIX + username;
        String token = (String) redisTemplate.opsForValue().get(tokenKey);
        
        if (token != null) {
            // 将token加入黑名单
            redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "1", 
                jwtUtils.extractExpiration(token).getTime() - System.currentTimeMillis(), 
                TimeUnit.MILLISECONDS);
            
            // 删除原token
            redisTemplate.delete(tokenKey);
            log.info("Token invalidated for user: {}", username);
        } else {
            log.warn("No token found for user: {}", username);
        }
    }
    
    private boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
} 