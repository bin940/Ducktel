package com.ducktel.config.security.jwt;

import com.ducktel.exception.CustomExpiredJwtException;
import com.ducktel.exception.CustomJwtException;
import com.nimbusds.jose.shaded.gson.Gson;
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
            "/api/sub-home/**",
            "/api/places/**"
    };


    private static void checkAuthorizationHeader(String header) {
        if (header == null) {
            throw new CustomJwtException(401, "MISSING_TOKEN", "토큰이 전달되지 않았습니다");
        } else if (!header.startsWith(JwtConstants.JWT_TYPE)) {
            throw new CustomJwtException(401, "INVALID_TOKEN_FORMAT", "BEARER로 시작하지 않는 올바르지 않은 토큰 형식입니다");
        }
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
            checkAuthorizationHeader(authHeader);   // header 가 올바른 형식인지 체크
            String token = JwtUtils.getTokenFromHeader(authHeader);
            // 토큰 만료 확인
            if (JwtUtils.isExpired(token)) {
                throw new CustomExpiredJwtException(401, "TOKEN_EXPIRED", "토큰이 만료되었습니다");
            }
            //토큰 검증 및 claims 가져오기
            Map<String, Object> claims = JwtUtils.validateToken(token);

            //토큰 만료까지 남은 시간
            long remainingMinutes = JwtUtils.tokenRemainTime((Integer) claims.get("exp"));
            log.info("Token remaining time: {} minutes", remainingMinutes);

            //토큰 만료 5분 전에 로그 출력
            if (remainingMinutes <= 5) {
                log.warn("Access Token is about to expire in {} minutes", remainingMinutes);
            }
            //토큰 인증 객체 생성
            Authentication authentication = JwtUtils.getAuthentication(token);

            log.info("authentication = {}", authentication);
            //인증 객체를 SecurityContext 에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);    // 다음 필터로 이동
        } catch (Exception e) {
            Gson gson = new Gson();
            String json = "";
            if (e instanceof CustomExpiredJwtException) {
                json = gson.toJson(Map.of("Token_Expired", e.getMessage()));
            } else {
                json = gson.toJson(Map.of("error", e.getMessage()));
            }

            response.setContentType("application/json; charset=UTF-8");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(json);
            printWriter.close();
        }
    }
}
