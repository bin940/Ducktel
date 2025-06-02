package com.ducktel.service;

import com.ducktel.config.security.jwt.JwtUtils;
import com.ducktel.domain.entity.User;
import com.ducktel.domain.repository.UserRepository;
import com.ducktel.dto.PrincipalDetailDTO;
import com.ducktel.exception.CustomJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {
    private final UserRepository userRepository;


     //JWT에서 userId, roles 추출 후 DB 조회하여 Authentication 생성
    public Authentication getAuthenticationFromToken(String token) {
        UUID userId = JwtUtils.extractUserId(token);
        String roles = JwtUtils.extractRoles(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomJwtException(401, "USER_NOT_FOUND", "유저를 찾을 수 없습니다."));

        List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(
                new PrincipalDetailDTO(user, authorities),
                null,
                authorities
        );
    }


    //JWT에서 userId만 빠르게 추출 (필요 시 서비스에서 직접 사용 가능)
    public UUID getUserIdFromToken(String token) {
        return JwtUtils.extractUserId(token);
    }


    //HTTP 요청 헤더에서 Authorization 토큰 추출
    public String getTokenFromHeader(String header) {
        return JwtUtils.getTokenFromHeader(header);
    }
}
