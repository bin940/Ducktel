package com.ducktel.config.security.jwt;

import com.ducktel.exception.CustomExpiredJwtException;
import com.ducktel.exception.CustomJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
@Slf4j
public class JwtUtils {
    private static final String SECRET_KEY = "1234567891234567891234123123123132";

    private static SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }


      //Authorization 헤더 "Bearer "에서 토큰 추출
    public static String getTokenFromHeader(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new CustomJwtException(401, "INVALID_TOKEN_FORMAT", "잘못된 Authorization 헤더 형식");
        }
        return header.substring(7);
    }


     //JWT 파싱 및 서명/만료 검증 후 Claims 반환
    public static Claims validateToken(String token) {
        log.debug("검증 중인 JWT: {}", token);
        log.debug("사용 중인 SECRET_KEY: {}", Base64.getEncoder().encodeToString(SECRET_KEY.getBytes()));
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("토큰 만료됨");
            throw new CustomExpiredJwtException(401, "TOKEN_EXPIRED", "토큰이 만료되었습니다.");
        } catch (JwtException e) {
            log.error("JWT 파싱 오류: {}", e.getMessage());
            throw new CustomJwtException(401, "INVALID_TOKEN", "유효하지 않은 JWT 토큰입니다.");
        }
    }


     // Claims에서 userId 추출
    public static UUID extractUserId(String token) {
        String userId = validateToken(token)
                .get("userId", String.class);
        if (userId == null) {
            throw new CustomJwtException(401, "INVALID_TOKEN", "userId 정보가 없습니다.");
        }
        return UUID.fromString(userId);
    }


      //Claims에서 roles 추출 (필수)
    public static String extractRoles(String token) {
        String roles = validateToken(token)
                .get("roles", String.class);
        if (roles == null) {
            throw new CustomJwtException(401, "INVALID_TOKEN", "roles 정보가 없습니다.");
        }
        return roles;
    }


      //토큰 남은 만료 시간(분) 계산
    public static long getRemainingMinutes(String token) {
        Date exp = validateToken(token).getExpiration();
        long diff = exp.getTime() - System.currentTimeMillis();
        return diff / (1000 * 60);
    }


     //새 JWT 생성 (claims에 userId, roles 등 포함)
    public static String generateToken(Map<String, Object> claims, int validMinutes) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(validMinutes).toInstant()))
                .signWith(getKey())
                .compact();
    }


    //토큰 타입 추출 (access, refresh 등)
    public static boolean isExpired(String token) {
        try {
            Date exp = validateToken(token).getExpiration();
            return exp.before(new Date());
        } catch (CustomExpiredJwtException e) {
            return true;
        }
    }


    // 토큰 타입 추출 (access, refresh 등)
    public static String getTokenType(String token) {
        Claims claims = validateToken(token);
        return claims.get("type", String.class);
    }
}