package com.ducktel.controller.auth;

import com.ducktel.config.security.service.AuthService;
import com.ducktel.dto.LoginRequestDTO;
import com.ducktel.dto.ResponseDTO;
import com.ducktel.exception.CustomException;
import com.ducktel.validation.CreateUser;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<Map<String, String>>> login(@Validated({CreateUser.class, Default.class}) @RequestBody LoginRequestDTO loginRequestDTO) {
        log.info("로그인 시도: {}", loginRequestDTO.getUsername());

        if (loginRequestDTO.getUsername() == null || loginRequestDTO.getUsername().isEmpty() ||
                loginRequestDTO.getPassword() == null || loginRequestDTO.getPassword().isEmpty()) {
            throw new CustomException("INVALID_REQUEST", "아이디와 비밀번호를 입력해주세요.");
        }

        Map<String,String> token = authService.login(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
        String accessToken = token.get("accessToken");
        String refreshToken = token.get("refreshToken");

        return ResponseEntity.ok(new ResponseDTO<>(200, null, "로그인 성공", Map.of("accessToken", accessToken,
                "refreshToken", refreshToken)));
    }
}
