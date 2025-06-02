package com.ducktel.config.security.handler;

import com.ducktel.config.security.jwt.JwtUtils;
import com.ducktel.domain.entity.User;
import com.ducktel.domain.repository.RefreshTokenRedisRepository;
import com.ducktel.dto.PrincipalDetailDTO;
import com.ducktel.dto.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FormLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        PrincipalDetailDTO principal = (PrincipalDetailDTO) authentication.getPrincipal();
        User user = principal.getUser();

        String accessToken = JwtUtils.generateToken(
                Map.of("userId", user.getUserId(), "roles", user.getRole(), "type", "access"),
                15
        );
        String refreshToken = JwtUtils.generateToken(
                Map.of("userId", user.getUserId(), "type", "refresh"),
                60 * 24 * 7
        );

        saveRefreshToken(user.getUserId(), refreshToken);

        // refreshToken을 HttpOnly 쿠키로 설정
        jakarta.servlet.http.Cookie refreshCookie = new jakarta.servlet.http.Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(refreshCookie);

        Map<String, String> responseMap = Map.of(
                "accessToken", accessToken,
                "loginType", "LOCAL"
        );

        ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>(200, null, "로그인 성공", responseMap);
        sendJsonResponse(response, responseDTO);
    }

    private void saveRefreshToken(UUID userId, String refreshToken) {
        refreshTokenRedisRepository.save(userId, refreshToken, 7L);
    }

    private void sendJsonResponse(HttpServletResponse response, ResponseDTO<?> responseDTO) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseDTO));
    }
}