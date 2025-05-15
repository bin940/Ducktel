package com.ducktel.config.security.hadler;

import com.ducktel.config.security.jwt.JwtUtils;
import com.ducktel.domain.entity.RefreshToken;
import com.ducktel.domain.entity.User;
import com.ducktel.domain.repository.RefreshTokenRepository;
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
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FormLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final RefreshTokenRepository refreshTokenRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        PrincipalDetailDTO principal = (PrincipalDetailDTO) authentication.getPrincipal();
        User user = principal.getUser();

        String accessToken = JwtUtils.generateToken(
                Map.of("userId", user.getUserId(), "roles", user.getRole(), "type", "access"),
                60
        );
        String refreshToken = JwtUtils.generateToken(
                Map.of("userId", user.getUserId(), "type", "refresh"),
                60 * 24 * 7
        );

        saveRefreshToken(user.getUserId(), refreshToken);

        Map<String, String> responseMap = Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "loginType", "LOCAL"
        );

        ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>(200, null, "로그인 성공", responseMap);
        sendJsonResponse(response, responseDTO);
    }

    private void saveRefreshToken(UUID userId, String refreshToken) {
        RefreshToken refreshEntity = new RefreshToken();
        refreshEntity.setUserId(userId);
        refreshEntity.setToken(refreshToken);
        refreshEntity.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(refreshEntity);
    }

    private void sendJsonResponse(HttpServletResponse response, ResponseDTO<?> responseDTO) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseDTO));
    }
}