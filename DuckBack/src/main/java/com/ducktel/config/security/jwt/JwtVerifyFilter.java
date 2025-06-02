package com.ducktel.config.security.jwt;

import com.ducktel.dto.ResponseDTO;
import com.ducktel.exception.CustomJwtException;
import com.ducktel.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtVerifyFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    private static final String[] whitelist = {
            "/api/users/register", "/", "/api/auth/login",
            "/favicon.ico", "/login",
            "/api/home", "/api/home/**", "/api/places/**"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        boolean skip = PatternMatchUtils.simpleMatch(whitelist, uri);
        log.debug("JwtVerifyFilter - skip filter for {}: {}", uri, skip);
        return skip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(JwtConstants.JWT_HEADER);
        try {
            // 토큰 형식, 유효성 검사
            String token = JwtUtils.getTokenFromHeader(header);
            long remain = JwtUtils.getRemainingMinutes(token);
            if (remain >= 0 && remain <= 5) {
                log.warn("Access Token이 {}분 후 만료됩니다.", remain);
            }

            // 인증 객체 생성 및 SecurityContext에 저장
            Authentication auth = jwtService.getAuthenticationFromToken(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);
        } catch (CustomJwtException e) {
            sendError(response, e.getStatusCode(), e.getErrorCode(), e.getMessage());
        } catch (ExpiredJwtException e) {
            sendError(response, 401, "TOKEN_EXPIRED", "토큰이 만료되었습니다.");
        }
    }

    private void sendError(HttpServletResponse res, int status, String code, String msg) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json; charset=UTF-8");
        ResponseDTO<?> error = new ResponseDTO<>(status, code, msg, null);
        new ObjectMapper().writeValue(res.getWriter(), error);
    }
}
