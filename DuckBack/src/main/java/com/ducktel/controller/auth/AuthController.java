package com.ducktel.controller.auth;

import com.ducktel.config.security.service.AuthService;
import com.ducktel.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;


    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        log.debug("토큰 갱신 요청 데이터: {}", request);

        Map<String, String> response = authService.refresh(request.get("refreshToken"));
        log.info("토큰 갱신 성공: {}", response);

        return ResponseEntity.ok(new ResponseDTO<>(200, null, "토큰 갱신 성공", response));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        log.debug("로그아웃 요청 데이터: {}", request);

        String refreshToken = request.get("refreshToken");
        String loginType = request.get("loginType");

        if (refreshToken == null || loginType == null) {
            log.warn("잘못된 로그아웃 요청: refreshToken={}, loginType={}", refreshToken, loginType);
            return ResponseEntity.ok(new ResponseDTO<>(400, "INVALID_REQUEST", "잘못된 요청", null));
        }

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

        if (logoutUrl == null) {
            log.warn("지원되지 않는 로그인 타입: loginType={}", loginType);
            return ResponseEntity.badRequest().body(new ResponseDTO<>(400, "INVALID_LOGIN_TYPE", "지원되지 않는 로그인 타입", null));
        }
        log.info("소셜 로그아웃 URL 반환: loginType={}, logoutUrl={}", loginType, logoutUrl);
        return ResponseEntity.ok(new ResponseDTO<>(200, null, "소셜 로그아웃 URL 반환", logoutUrl));
    }
}


