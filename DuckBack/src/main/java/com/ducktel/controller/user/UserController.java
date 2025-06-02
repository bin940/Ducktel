package com.ducktel.controller.user;

import com.ducktel.dto.ResponseDTO;
import com.ducktel.dto.UserDTO;
import com.ducktel.service.JwtService;
import com.ducktel.service.UserService;
import com.ducktel.validation.CreateUser;
import com.ducktel.validation.PasswordReset;
import com.ducktel.validation.UpdateUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<?>> registerUser(@Validated(CreateUser.class) @RequestBody UserDTO userDTO) {
        log.debug("회원가입 요청 데이터: {}", userDTO);

        String userName =userService.registerUser(userDTO);
        log.info("회원가입 완료: username={}", userName);

            return ResponseEntity.ok(new ResponseDTO<>(200, null, "회원가입 성공", userName));
    }
    @GetMapping("/profile")
    public ResponseEntity<ResponseDTO<?>> getProfile(HttpServletRequest request){
        String token = jwtService.getTokenFromHeader(request.getHeader("Authorization"));
        log.debug("Authorization 헤더에서 추출한 토큰: {}", token);

        UUID userId = jwtService.getUserIdFromToken(token);

        if (userId == null) {
            log.warn("유효하지 않은 Authorization 헤더로 요청: token={}", token);
            return ResponseEntity.ok(new ResponseDTO<>(400, "AUTH_MISSING", "Authorization 헤더가 누락되었거나 유효하지 않습니다", null));
        }

        UserDTO user = userService.getProfile(userId);
        log.info("유저 프로필 조회 성공: userId={}", userId);
        return ResponseEntity.ok(new ResponseDTO<>(200, null, "유저정보 조회 성공", user));
    }
    @PutMapping("/profile")
    public ResponseEntity<ResponseDTO<?>> updateProfile(HttpServletRequest request, @Validated(UpdateUser.class) @RequestBody UserDTO userData){
        log.debug("프로필 업데이트 요청 데이터: {}", userData);

        String token = jwtService.getTokenFromHeader(request.getHeader("Authorization"));
        UUID userId = jwtService.getUserIdFromToken(token);

        UserDTO user = userService.updateProfile(userId, userData);
        log.info("프로필 업데이트 성공: userId={}", userId);

        return ResponseEntity.ok(new ResponseDTO<>(200, null, "유저정보 변경 성공", user));
    }
    @DeleteMapping("/profile/{userId}")
    public ResponseEntity<ResponseDTO<?>> deleteProfile(@PathVariable("userId") UUID userId){
        String result =userService.deleteProfile(userId);
        return ResponseEntity.ok(new ResponseDTO<>(200, null, result, null));
    }

    @PostMapping("/password-reset")
    public ResponseEntity<ResponseDTO<?>> passWordReset(HttpServletRequest request, @Validated(PasswordReset.class) @RequestBody UserDTO user) {
        log.debug("비밀번호 재설정 요청 데이터: {}", user);

        String token = jwtService.getTokenFromHeader(request.getHeader("Authorization"));
        UUID userId = jwtService.getUserIdFromToken(token);
        String newPassword = user.getPassword();
        String result =userService.passWordReset(userId, newPassword);
        return ResponseEntity.ok(new ResponseDTO<>(200, null, result, null));

    }
    @PostMapping("/likes")
    public ResponseEntity<String> toggleLike(HttpServletRequest request,
                                             @RequestParam Long accommodationId) {
        log.debug("좋아요 토글 요청: accommodationId={}", accommodationId);

        String token = jwtService.getTokenFromHeader(request.getHeader("Authorization"));
        UUID userId = jwtService.getUserIdFromToken(token);
        String result = userService.toggleLike(userId, accommodationId);
        return ResponseEntity.ok(result);
    }
}
