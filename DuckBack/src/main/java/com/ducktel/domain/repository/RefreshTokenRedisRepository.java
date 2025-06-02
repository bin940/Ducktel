package com.ducktel.domain.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {
    private final StringRedisTemplate redisTemplate;

    public void save(UUID userId, String refreshToken, long days) {
        redisTemplate.opsForValue().set(
                "refresh:" + userId,
                refreshToken,
                Duration.ofDays(days)
        );
    }
    public String get(UUID userId) {
        return redisTemplate.opsForValue().get("refresh:" + userId);
    }

    public void delete(UUID userId) {
        redisTemplate.delete("refresh:" + userId);
    }
}