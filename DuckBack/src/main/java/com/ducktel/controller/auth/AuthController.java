package com.ducktel.controller.auth;

import com.ducktel.config.security.cookie.util.CookieUtils;
import com.ducktel.config.security.service.AuthService;
import com.ducktel.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;



    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            jakarta.servlet.http.HttpServletResponse response) {
        log.debug("토큰 갱신 요청: refreshToken={}", refreshToken);

        if (refreshToken == null) {
            return ResponseEntity.badRequest().body(new ResponseDTO<>(400, "NO_REFRESH_TOKEN", "refreshToken 쿠키가 없습니다.", null));
        }

        Map<String, String> tokens = authService.refresh(refreshToken);
        String newAccessToken = tokens.get("accessToken");
        String newRefreshToken = tokens.get("refreshToken");

        // 새 refreshToken을 HttpOnly 쿠키로 재설정
        CookieUtils.addCookie(response, "refreshToken", newRefreshToken, 60 * 60 * 24 * 7);

        log.info("토큰 갱신 성공: accessToken={}", newAccessToken);

        // 응답 본문에는 accessToken만 포함
        return ResponseEntity.ok(new ResponseDTO<>(200, null, "토큰 갱신 성공", Map.of("accessToken", newAccessToken)));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            @RequestBody Map<String, String> request,
            jakarta.servlet.http.HttpServletResponse response) {
        log.debug("로그아웃 요청: refreshToken={}, request={}", refreshToken, request);

        String loginType = request.get("loginType");

        if (refreshToken == null || loginType == null) {
            log.warn("잘못된 로그아웃 요청: refreshToken={}, loginType={}", refreshToken, loginType);
            return ResponseEntity.ok(new ResponseDTO<>(400, "INVALID_REQUEST", "잘못된 요청", null));
        }

        // refreshToken 쿠키 만료 처리
        CookieUtils.deleteCookie(response, "refreshToken");

        if ("LOCAL".equalsIgnoreCase(loginType)) {
            authService.logout(refreshToken);

            log.info("로컬 로그아웃 성공: refreshToken={}", refreshToken);
            return ResponseEntity.ok(new ResponseDTO<>(200, null, "로그아웃 성공", null));
        }

        Map<String, String> logoutUrls = new HashMap<>();
        logoutUrls.put("GOOGLE", "https://accounts.google.com/logout");
        logoutUrls.put("KAKAO", "https://kauth.kakao.com/oauth/logout");
        logoutUrls.put("NAVER", "https://nid.naver.com/nidlogin.logout");

        String logoutUrl = logoutUrls.get(loginType.toUpperCase());
        authService.logout(refreshToken);
        if (logoutUrl == null) {
            log.warn("지원되지 않는 로그인 타입: loginType={}", loginType);
            return ResponseEntity.badRequest().body(new ResponseDTO<>(400, "INVALID_LOGIN_TYPE", "지원되지 않는 로그인 타입", null));
        }
        log.info("소셜 로그아웃 URL 반환: loginType={}, logoutUrl={}", loginType, logoutUrl);
        return ResponseEntity.ok(new ResponseDTO<>(200, null, "소셜 로그아웃 URL 반환", logoutUrl));
    }
}


