package com.ducktel.controller.user;

import com.ducktel.domain.repository.UserRepository;
import com.ducktel.dto.UserDTO;
import com.ducktel.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("회원가입 시도: {}", userDTO.getUsername());
        try {
            String UserName =userService.registerUser(userDTO);
            return ResponseEntity.ok(UserName+"님 회원가입을 축하합니다.");
        } catch (Exception e) {
            log.error("회원가입 실패: {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 실패!" + e.getMessage());
        }
    }
}
