package com.ducktel.config.security.service;

import com.ducktel.config.security.jwt.JwtUtils;
import com.ducktel.domain.repository.RefreshTokenRedisRepository;
import com.ducktel.exception.CustomJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public Map<String, String> refresh(String refreshToken) {
        log.debug("리프레시 토큰 갱신 요청: refreshToken={}", refreshToken);


        if (JwtUtils.isExpired(refreshToken) || !"refresh".equals(JwtUtils.getTokenType(refreshToken))) {
            log.warn("리프레시 토큰 갱신 실패: 유효하지 않은 토큰");
            throw new CustomJwtException(401, "INVALID_TOKEN", "토큰이 null입니다.");
        }

        Map<String, Object> claims = JwtUtils.validateToken(refreshToken);
        String userIdStr = (String) claims.get("userId");
        UUID userId = UUID.fromString(userIdStr);

        String storedToken = refreshTokenRedisRepository.get(userId);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            log.warn("리프레시 토큰 갱신 실패: 저장된 토큰과 불일치");
            throw new CustomJwtException(401, "INVALID_TOKEN", "저장된 리프레시 토큰이 존재하지 않거나 일치하지 않습니다.");
        }

        // 새 Token 생성
        String newAccessToken = JwtUtils.generateToken(
                Map.of("userId", userId, "roles", "ROLE_USER", "type", "access"),
                15
        );
        String newRefreshToken = JwtUtils.generateToken(
                Map.of("userId", userId, "type", "refresh"),
                60 * 24 * 7
        );
        refreshTokenRedisRepository.save(userId, newRefreshToken, 7L);
        log.info("새 AccessToken 생성 성공: userId={}", userId);

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }

    public void logout(String refreshToken) {
        log.debug("로그아웃 요청: refreshToken={}", refreshToken);

        Map<String, Object> claims = JwtUtils.validateToken(refreshToken);
        String userIdStr = (String) claims.get("userId");
        UUID userId = UUID.fromString(userIdStr);
        log.debug("로그아웃 사용자 ID 추출: userId={}", userId);

        refreshTokenRedisRepository.delete(userId);
        log.info("로그아웃 성공: refreshToken 삭제 완료");
    }
}
