package com.ducktel.config.security.service;

import com.ducktel.config.security.jwt.JwtUtils;
import com.ducktel.domain.entity.RefreshToken;
import com.ducktel.domain.repository.RefreshTokenRepository;
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
        String userIdstr = (String) claims.get("userId");
        UUID userId = UUID.fromString(userIdstr);
        log.debug("토큰에서 사용자 정보 추출 성공: userId={}", userId);

        // 새 Token 생성
        String newAccessToken = JwtUtils.generateToken(
                Map.of("userId", userId, "roles", "ROLE_USER", "type", "access"),
                60
        );
        String newRefreshToken = JwtUtils.generateToken(
                Map.of("userId", userId, "type", "refresh"),
                60 * 24 * 7
        );
        refreshTokenRepository.updateTokenByUserId(
                userId,
                newRefreshToken,
                LocalDateTime.now().plusDays(7)
        );
        log.info("새 AccessToken 생성 성공: userId={}", userId);

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }

    public void logout(String refreshToken) {
        log.debug("로그아웃 요청: refreshToken={}", refreshToken);

        Optional<RefreshToken> storedToken = refreshTokenRepository.findByToken(refreshToken);
        if (storedToken.isPresent()) {
            refreshTokenRepository.delete(storedToken.get());
            log.info("로그아웃 성공: refreshToken 삭제 완료");
        } else {
            log.warn("로그아웃 실패: 저장된 토큰 없음");
            throw new CustomJwtException(401, "INVALID_REFRESH_TOKEN", "저장된 리프레시 토큰이 존재하지 않습니다.");
        }
    }
}
