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
        log.debug("리프레시 토큰 갱신 요청: refreshToken={}", refreshToken);

        // DB에서 리프레시 토큰 확인
        Optional<RefreshToken> storedToken = refreshTokenRepository.findByToken(refreshToken);
        if (storedToken.isEmpty() || JwtUtils.isExpired(refreshToken) || !"refresh".equals(JwtUtils.getTokenType(refreshToken))) {
            log.warn("리프레시 토큰 갱신 실패: 유효하지 않은 토큰");
            throw new CustomJwtException(401, "INVALID_TOKEN", "토큰이 null입니다.");
        }
        log.debug("리프레시 토큰 확인 성공");

        // 토큰에서 사용자 정보 추출
        Map<String, Object> claims = JwtUtils.validateToken(refreshToken);
        String userId = (String) claims.get("userId");
        String username = (String) claims.get("username");
        log.debug("토큰에서 사용자 정보 추출 성공: userId={}, username={}", userId, username);

        // 새 accessToken 생성
        String newAccessToken = JwtUtils.generateToken(
                Map.of("userId", userId, "username", username, "roles", "ROLE_USER", "type", "access"),
                60
        );
        log.info("새 AccessToken 생성 성공: userId={}", userId);

        return Map.of("accessToken", newAccessToken);
    }

    public void logout(String refreshToken) {
        log.debug("로그아웃 요청: refreshToken={}", refreshToken);

        Optional<RefreshToken> storedToken = refreshTokenRepository.findByToken(refreshToken);
        if (storedToken.isPresent()) {
            refreshTokenRepository.delete(storedToken.get());
            log.info("로그아웃 성공: refreshToken 삭제 완료");
        } else {
            log.warn("로그아웃 실패: 저장된 토큰 없음");
        }
    }
}
