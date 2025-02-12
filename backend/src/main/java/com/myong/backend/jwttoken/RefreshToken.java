package com.myong.backend.jwttoken;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "refreshToken")
public class RefreshToken {

    @Id
    private String key; // Redis 해시고유키

    private String refreshToken;

    @TimeToLive(unit = TimeUnit.DAYS)
    private Long timeToLive; // 일 단위 설정

}
