package com.ducktel.controller.user;

import com.ducktel.config.security.jwt.JwtUtils;
import com.ducktel.domain.repository.UserRepository;
import com.ducktel.dto.UserDTO;
import com.ducktel.service.UserService;
import com.ducktel.validation.UpdateUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(HttpServletRequest request){
        String token = JwtUtils.getTokenFromHeader(request.getHeader("Authorization"));
        Long userId = JwtUtils.getUserIdFromToken(token);
        UserDTO user = userService.getProfile(userId);
        return ResponseEntity.ok(user);
    }
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(HttpServletRequest request, @Validated({UpdateUser.class, Default.class}) @RequestBody UserDTO userData){
        String token = JwtUtils.getTokenFromHeader(request.getHeader("Authorization"));
        Long userId = JwtUtils.getUserIdFromToken(token);
        UserDTO user = userService.updateProfile(userId, userData);
        return ResponseEntity.ok(user);
    }
}
