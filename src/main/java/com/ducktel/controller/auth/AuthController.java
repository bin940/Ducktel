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
}


