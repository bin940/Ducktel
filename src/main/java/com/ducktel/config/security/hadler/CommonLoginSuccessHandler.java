package com.ducktel.config.security.hadler;

import com.ducktel.config.security.jwt.JwtConstants;
import com.ducktel.config.security.jwt.JwtUtils;
import com.ducktel.dto.PrincipalDetailDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CommonLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        PrincipalDetailDTO principal = (PrincipalDetailDTO) authentication.getPrincipal();


        Map<String, Object> responseMap = principal.getUserInfo();
        String accessToken = JwtUtils.generateToken(responseMap, JwtConstants.ACCESS_EXP_TIME);
        String refreshToken = JwtUtils.generateToken(responseMap, JwtConstants.REFRESH_EXP_TIME);

        response.sendRedirect("http://localhost:3000/oauth/callback?accessToken=" + accessToken + "&refreshToken=" + refreshToken);
    }
}
