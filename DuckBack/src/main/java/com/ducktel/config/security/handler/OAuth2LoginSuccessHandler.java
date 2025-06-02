package com.ducktel.config.security.handler;

import com.ducktel.config.security.jwt.JwtUtils;
import com.ducktel.domain.entity.User;
import com.ducktel.domain.repository.RefreshTokenRedisRepository;
import com.ducktel.dto.PrincipalDetailDTO;
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
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

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

        String loginType = user.getProvider().toUpperCase();

        jakarta.servlet.http.Cookie refreshCookie = new jakarta.servlet.http.Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setDomain("ducktel.uk");
        refreshCookie.setMaxAge(60 * 60 * 24 * 7);

        response.addCookie(refreshCookie);

        String redirectUrl = "https://www.ducktel.uk/login" +
                "?accessToken=" + accessToken +
                "&loginType=" + loginType;

        response.sendRedirect(redirectUrl);
    }

    private void saveRefreshToken(UUID userId, String refreshToken) {
        refreshTokenRedisRepository.save(userId, refreshToken, 7);
    }
}