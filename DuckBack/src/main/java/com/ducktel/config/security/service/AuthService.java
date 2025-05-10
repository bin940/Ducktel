package com.ducktel.config.security.service;

import com.ducktel.config.security.jwt.JwtUtils;
import com.ducktel.domain.entity.RefreshToken;
import com.ducktel.domain.repository.RefreshTokenRepository;
import com.ducktel.exception.CustomJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;

    public Map<String, String> refresh(String refreshToken) {
        // DB에서 리프레시 토큰 확인
        Optional<RefreshToken> storedToken = refreshTokenRepository.findByToken(refreshToken);
        if (storedToken.isEmpty() || JwtUtils.isExpired(refreshToken) || !"refresh".equals(JwtUtils.getTokenType(refreshToken))) {
            throw new CustomJwtException(401, "INVALID_TOKEN", "토큰이 null입니다.");
        }

        // 토큰에서 사용자 정보 추출
        Map<String, Object> claims = JwtUtils.validateToken(refreshToken);
        String userId = (String) claims.get("userId");
        String username = (String) claims.get("username");

        // 새 accessToken 생성
        String newAccessToken = JwtUtils.generateToken(
                Map.of("userId", userId, "username", username, "roles", "ROLE_USER", "type", "access"), // roles는 필요 시 DB에서
                60
        );

        return Map.of("accessToken", newAccessToken);
    }

    public void logout(String refreshToken) {
        Optional<RefreshToken> storedToken = refreshTokenRepository.findByToken(refreshToken);
        storedToken.ifPresent(refreshTokenRepository::delete);
    }
}
