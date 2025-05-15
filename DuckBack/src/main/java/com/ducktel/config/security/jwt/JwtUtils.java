package com.ducktel.config.security.jwt;

import com.ducktel.domain.entity.User;
import com.ducktel.dto.PrincipalDetailDTO;
import com.ducktel.exception.CustomException;
import com.ducktel.exception.CustomExpiredJwtException;
import com.ducktel.exception.CustomJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class JwtUtils {
    //나중에 환경변수 변경
    private static final String SECRET_KEY = "1234567891234567891234123123123132";


    private static SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // Bearer 토큰 파싱
    public static String getTokenFromHeader(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("잘못된 Authorization 헤더 형식");
        }
        return header.split(" ")[1]; // "Bearer " 이후의 토큰 반환
    }


    // 토큰 발급
    public static String generateToken(Map<String, Object> claims, int validMinutes) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(validMinutes).toInstant()))
                .signWith(getKey())
                .compact();
    }

    //SecurityContextHolder 에 저장
    public static Authentication getAuthentication(String token) {
        Claims claims = validateToken(token);

        // GUID 형태로 변경 보안성
        String email = (String) claims.get("email");
        String name = (String) claims.get("username");
        String roles = (String) claims.get("roles");

        List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User user = User.builder().email(email).username(name).role(roles).build();

        return new UsernamePasswordAuthenticationToken(
                new PrincipalDetailDTO(user, authorities), "", authorities);
    }
    // jwt userId 따로 사용
    public static UUID getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        String userIdStr = claims.get("userId", String.class);
        if (userIdStr == null) {
            throw new CustomJwtException(401, "INVALID_TOKEN", "userId 정보가 없습니다.");
        }
        return UUID.fromString(userIdStr);
    }


    public static String getTokenType(String token) {
        Claims claims = validateToken(token);
        String type = claims.get("type", String.class);
        if (type == null) {
            throw new CustomJwtException(401, "INVALID_TOKEN", "토큰에 type 정보가 없습니다.");
        }
        return type;
    }


    public static Claims validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            throw new CustomExpiredJwtException(401, "TOKEN_EXPIRED", "토큰이 만료되었습니다.");
        } catch (JwtException e) {
            throw new CustomJwtException(401, "INVALID_TOKEN", "유효하지 않은 JWT 토큰입니다: ");
        } catch (Exception e) {
            throw new CustomJwtException(401, "JWT_VERIFICATION_FAILED", "JWT 검증 실패: " );
        }
    }

    // 토큰이 만료되었는지 판단
    public static boolean isExpired(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.getExpiration().before(new Date());
        } catch (CustomExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false; // 다른 검증 실패는 만료 로 판단하지 않음
        }
    }

    // 토큰의 남은 만료시간 계산
    public static long getRemainingMinutes(String token) {
        Claims claims = validateToken(token);
        Date expiration = claims.getExpiration();
        long diffMs = expiration.getTime() - System.currentTimeMillis();
        return diffMs / (1000 * 60);
    }
}