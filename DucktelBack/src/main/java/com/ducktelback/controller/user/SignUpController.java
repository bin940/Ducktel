package com.ducktelback.controller.user;

import com.ducktelback.dto.SignupRequest;
import com.ducktelback.service.UserService;
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
@RequestMapping("/user")
@Slf4j
public class SignUpController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignupRequest signupRequest) {
        log.info("회원가입 시도: {}", signupRequest.getUsername());
        try {
            userService.signUp(signupRequest);
            return ResponseEntity.ok("회원가입 성공");
        } catch (Exception e) {
            log.error("회원가입 실패: {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 실패" + e.getMessage());
        }
    }



}
