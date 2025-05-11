package com.ducktel.service;

import com.ducktel.config.security.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtService {

    public String getTokenFromHeader(String header) {
        log.debug("JWT 헤더에서 토큰 추출 요청: header={}", header);
        String token = JwtUtils.getTokenFromHeader(header);
        log.info("JWT 토큰 추출 성공");
        return token;
    }

    public String getUserIdFromToken(String token) {
        log.debug("JWT 토큰에서 사용자 ID 추출 요청: token={}", token);
        String userId = JwtUtils.getUserIdFromToken(token);
        log.info("JWT 사용자 ID 추출 성공: userId={}", userId);
        return userId;
    }
}
