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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtUtils {
    //나중에 환경변수 변경
    private static final String SECRET_KEY = "1234567891234567891234123123123132";


    public static String getTokenFromHeader(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("잘못된 Authorization 헤더 형식");
        }
        return header.split(" ")[1]; // "Bearer " 이후의 토큰 반환
    }



    public static String generateToken(Map<String, Object> claims, int validTime) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(validTime).toInstant()))
                .signWith(key)
                .compact();
    }
    //SecurityContextHolder 에 저장
    public static Authentication getAuthentication(String token) {
        Map<String, Object> claims = validateToken(token);

        // GUID 형태로 변경 보안성
        String email = (String) claims.get("email");
        String name = (String) claims.get("username");
        String roles = (String) claims.get("roles");

        List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());



        User user = User.builder().email(email).username(name).role(roles).build();

        PrincipalDetailDTO principalDetailDTO = new PrincipalDetailDTO(user, authorities);

        return new UsernamePasswordAuthenticationToken(principalDetailDTO, "", authorities);
    }
    // jwt userId 따로 사용
    public static String getUserIdFromToken(String token) {
        if (token == null) {
            throw new CustomException("INVALID_TOKEN", "토큰이 null입니다.");
        }
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String userId = claims.get("userId", String.class);
            System.out.println("JwtUtils - 추출된 userId: " + userId); // 디버깅
            if (userId == null) {
                throw new CustomException("INVALID_TOKEN", "토큰에 userId가 없습니다.");
            }
            return userId;
        } catch (Exception e) {
            throw new CustomException("INVALID_TOKEN", "토큰 파싱 실패: " + e.getMessage());
        }
    }

    public static Map<String, Object> validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            throw new CustomExpiredJwtException("토큰이 만료되었습니다.", e);
        }catch (JwtException e) {
            throw new CustomJwtException("유효하지 않은 JWT 토큰입니다:"+ e.getMessage());
        } catch (Exception e) {
            throw new CustomJwtException("JWT 검증 실패: " + e.getMessage());
        }
    }

    // 토큰이 만료되었는지 판단
    public static boolean isExpired(String token) {
        try {
            validateToken(token);
        } catch (CustomExpiredJwtException e) {
            return true;//  CustomExpiredJwtException이 발생하면 만료된 토큰으로 판단
        } catch (CustomJwtException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ExpiredJwtException) {
                return true; //CustomJwtException 내부 cause가 ExpiredJwtException이면 만료된 토큰으로 판단
            }
        }
        return false;
    }

    // 토큰의 남은 만료시간 계산
    public static long tokenRemainTime(Integer expTime) {
        Date expDate = new Date((long) expTime * (1000));
        long remainMs = expDate.getTime() - System.currentTimeMillis();
        return remainMs / (1000 * 60);
    }
}