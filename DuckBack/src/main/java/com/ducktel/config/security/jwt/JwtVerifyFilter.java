package com.ducktel.config.security.jwt;

import com.ducktel.dto.ResponseDTO;
import com.ducktel.exception.CustomExpiredJwtException;
import com.ducktel.exception.CustomJwtException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtVerifyFilter extends OncePerRequestFilter {

    //jwt 필터를 거치지 않을 URL 을 설정
    private static final String[] whitelist = {
            "/api/users/register",
            "/",
            "/api/auth/login",
            "/favicon.ico",
            "/login",
            "/api/home",
            "/api/home/**",
            "/api/places/**"
    };


    private static void checkAuthorizationHeader(String header) {
        if (header == null) {
            throw new CustomJwtException(401, "MISSING_TOKEN", "토큰이 전달되지 않았습니다");
        } else if (!header.startsWith(JwtConstants.JWT_TYPE)) {
            throw new CustomJwtException(401, "INVALID_TOKEN_FORMAT", "BEARER로 시작하지 않는 올바르지 않은 토큰 형식입니다");
        }
    }

    private void handleJwtError(HttpServletResponse response, int status, String errorCode, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setContentType("application/json; charset=UTF-8");
        ResponseDTO<?> error = new ResponseDTO<>(status, errorCode, message, null);
        new ObjectMapper().writeValue(response.getWriter(), error);
    }

    // 필터를 거치지 않을 URL 을 설정하고, true 를 return 하면 현재 필터를 건너뛰고 다음 필터로 이동
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String servletPath = request.getServletPath();
        String requestURI = request.getRequestURI();
        boolean shouldSkip = PatternMatchUtils.simpleMatch(whitelist, requestURI);
        log.info("JwtVerifyFilter - Request URI: {}", uri);
        log.info("JwtVerifyFilter - Servlet Path: {}", servletPath);
        log.info("Checking shouldNotFilter for URI: {} | Should Skip: {}", requestURI, shouldSkip);
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        log.info("--------------------------- JwtVerifyFilter ---------------------------");

        String authHeader = request.getHeader(JwtConstants.JWT_HEADER);
        String requestURI = request.getRequestURI();
        log.info("Request URI: {} | Should Skip: false", requestURI);

        try {
            // 헤더 유효성 검사
            checkAuthorizationHeader(authHeader);

            // 토큰 추출
            String token = JwtUtils.getTokenFromHeader(authHeader);

            long remainingMinutes = JwtUtils.getRemainingMinutes(token);

            log.info("Token remaining time: {} minutes", remainingMinutes);
            if (remainingMinutes >= 0 && remainingMinutes <= 5) {
                log.warn("Access Token is about to expire in {} minutes", remainingMinutes);
            }

            // 인증 객체 생성 및 SecurityContext에 저장
            Authentication authentication = JwtUtils.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 다음 필터로 이동
            filterChain.doFilter(request, response);

        } catch (CustomJwtException e) {
            handleJwtError(response, e.getStatusCode(), e.getErrorCode(), e.getMessage());
        } catch (ExpiredJwtException e) {
            handleJwtError(response, 401, "TOKEN_EXPIRED", "토큰이 만료되었습니다.");
        } catch (JwtException e) {
            handleJwtError(response, 401, "INVALID_TOKEN", "유효하지 않은 JWT 토큰입니다.");
        } catch (Exception e) {
            handleJwtError(response, 500, "INTERNAL_SERVER_ERROR", "서버 내부 오류: " + e.getMessage());
        }
    }
}
