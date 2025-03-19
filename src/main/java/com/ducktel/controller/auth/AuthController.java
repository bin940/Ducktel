package com.ducktel.controller.auth;

import com.ducktel.config.security.service.AuthService;
import com.ducktel.dto.LoginRequestDTO;
import com.ducktel.dto.ResponseDTO;
import com.ducktel.exception.CustomException;
import com.ducktel.validation.CreateUser;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        Map<String, String> response = authService.refresh(request.get("refreshToken"));
        return ResponseEntity.ok(new ResponseDTO<>(200, null, "토큰 갱신 성공", response));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String loginType = request.get("loginType");

        if (refreshToken == null || loginType == null) {
            return ResponseEntity.badRequest().body(new ResponseDTO<>(400, "INVALID_REQUEST", "잘못된 요청", null));
        }

        if ("LOCAL".equalsIgnoreCase(loginType)) {
            authService.logout(refreshToken);
            return ResponseEntity.ok(new ResponseDTO<>(200, null, "로그아웃 성공", null));
        }

        Map<String, String> logoutUrls = new HashMap<>();
        logoutUrls.put("GOOGLE", "https://accounts.google.com/logout");
        logoutUrls.put("KAKAO", "https://kauth.kakao.com/oauth/logout");
        logoutUrls.put("NAVER", "https://nid.naver.com/nidlogin.logout");

        String logoutUrl = logoutUrls.get(loginType.toUpperCase());

        if (logoutUrl == null) {
            return ResponseEntity.badRequest().body(new ResponseDTO<>(400, "INVALID_LOGIN_TYPE", "지원되지 않는 로그인 타입", null));
        }

        return ResponseEntity.ok(new ResponseDTO<>(200, null, "소셜 로그아웃 URL 반환", logoutUrl));
    }
}


