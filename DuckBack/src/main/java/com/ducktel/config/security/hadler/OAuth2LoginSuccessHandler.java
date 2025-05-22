package com.ducktel.config.security.hadler;

import com.ducktel.config.security.jwt.JwtUtils;
import com.ducktel.domain.entity.RefreshToken;
import com.ducktel.domain.entity.User;
import com.ducktel.domain.repository.RefreshTokenRepository;
import com.ducktel.dto.PrincipalDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final RefreshTokenRepository refreshTokenRepository;

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

        String loginType = user.getProvider().toUpperCase();

        String redirectUrl = "https://www.ducktel.uk/login" +
                "?accessToken=" + accessToken +
                "&refreshToken=" + refreshToken +
                "&loginType=" + loginType;

        response.sendRedirect(redirectUrl);
    }

    private void saveRefreshToken(UUID userId, String refreshToken) {
        RefreshToken refreshEntity = new RefreshToken();
        refreshEntity.setUserId(userId);
        refreshEntity.setToken(refreshToken);
        refreshEntity.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(refreshEntity);
    }
}