package com.myong.backend.jwttoken;

import com.myong.backend.jwttoken.dto.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
public class JwtService {
    private final Key key;
    private final RedisTemplate<String, Object> redisTemplate;


    public JwtService(@Value("${jwt.secret}") String secretKey, RedisTemplate<String, Object> redisTemplate) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisTemplate = redisTemplate;
    }

    public String createAccessToken (String userName, String role){
        long now = (new Date()).getTime();
        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + 1800000);
        return Jwts.builder()
                .setSubject(userName)
                .claim("auth", role)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public void saveRedisRefreshToken(String userName){
        RefreshToken refreshToken = new RefreshToken(
                userName,
                UUID.randomUUID().toString(),
                7L
        );
        redisTemplate.opsForValue().set(refreshToken.getKey(),refreshToken,refreshToken.getTimeToLive(), TimeUnit.DAYS);
    }
    public void deleteRedisRefreshToken(String userName){
        redisTemplate.delete(userName);
    }

    public boolean isExpired(String token){
        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date exp = claims.getExpiration();
            return exp.before(new Date());
        }catch (Exception e){
            return true;
        }
    }
    public boolean refreshTokenIsExpired(String userName){

        // Redis에서 리프레시 토큰을 가져오기
        RefreshToken refreshToken = (RefreshToken) redisTemplate.opsForValue().get(userName);

        // 토큰이 없으면 만료된 것으로 간주
        return refreshToken != null;
    }

    public String getUserName(String token){
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    public String getUserRole(String token){
        Claims claims = parseClaims(token);
        return claims.get("Auth",String.class);
    }


    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
