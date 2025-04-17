package com.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * <p>
 * 此工具类用于 JWT (JSON Web Token) 的创建、解析和验证。
 * 它提供了生成令牌、从令牌中提取信息以及验证令牌有效性的功能。
 * 使用 HMAC SHA-256 算法进行签名。
 * </p>
 */
@Component
public class JwtUtils {
    
    /**
     * JWT 密钥，从配置文件中注入
     */
    @Value("${jwt.secret}")
    private String secret;
    
    /**
     * JWT 过期时间（秒），从配置文件中注入
     */
    @Value("${jwt.expiration}")
    private Long expiration;
    
    /**
     * 获取用于签名和验证的密钥对象
     *
     * @return SecretKey 用于 JWT 签名和验证的密钥
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    /**
     * 为指定用户生成 JWT 令牌
     *
     * @param username 用户名
     * @return 生成的 JWT 令牌字符串
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }
    
    /**
     * 为指定用户ID生成 JWT 令牌
     * 
     * @param userId 用户ID
     * @return 生成的 JWT 令牌字符串
     */
    public String generateTokenForUserId(Integer userId) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userId.toString());
    }
    
    /**
     * 创建 JWT 令牌
     *
     * @param claims 要包含在令牌中的声明
     * @param subject 令牌的主题（通常是用户名或用户ID）
     * @return 创建的 JWT 令牌字符串
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 从 JWT 令牌中提取所有声明
     *
     * @param token JWT 令牌
     * @return 令牌中包含的所有声明
     * @throws io.jsonwebtoken.JwtException 如果令牌无效或已过期
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 从令牌中提取用户名
     *
     * @param token JWT 令牌
     * @return 令牌中的用户名
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }
    
    /**
     * 从令牌中提取过期时间
     *
     * @param token JWT 令牌
     * @return 令牌的过期时间
     */
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
    
    /**
     * 检查令牌是否已过期
     *
     * @param token JWT 令牌
     * @return 如果令牌已过期则返回 true，否则返回 false
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * 验证令牌是否有效
     * <p>
     * 验证包括检查令牌中的用户名是否与给定用户名匹配，以及令牌是否已过期。
     * </p>
     *
     * @param token JWT 令牌
     * @param username 要验证的用户名
     * @return 如果令牌有效则返回 true，否则返回 false
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
} 