package com.ducktel.controller.user;

import com.ducktel.config.security.jwt.JwtUtils;
import com.ducktel.dto.BookingDetailDTO;
import com.ducktel.dto.ResponseDTO;
import com.ducktel.dto.UserDTO;
import com.ducktel.service.BookingService;
import com.ducktel.service.JwtService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    private final UserService userService;
    private final BookingService bookingService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<?>> registerUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("회원가입 시도: {}", userDTO.getUsername());

            String userName =userService.registerUser(userDTO);
            return ResponseEntity.ok(new ResponseDTO<>(200, null, "회원가입 성공", userName));
    }
    @GetMapping("/profile")
    public ResponseEntity<ResponseDTO<?>> getProfile(HttpServletRequest request){
        String token = jwtService.getTokenFromHeader(request.getHeader("Authorization"));
        String userId = jwtService.getUserIdFromToken(token);

        if (userId == null) {
            return ResponseEntity.ok(new ResponseDTO<>(400, "AUTH_MISSING", "Authorization 헤더가 누락되었거나 유효하지 않습니다", null));
        }

        UserDTO user = userService.getProfile(userId);
        return ResponseEntity.ok(new ResponseDTO<>(200, null, "유저정보 조회 성공", user));
    }
    @PutMapping("/profile")
    public ResponseEntity<ResponseDTO<?>> updateProfile(HttpServletRequest request, @Validated({UpdateUser.class, Default.class}) @RequestBody UserDTO userData){
        String token = jwtService.getTokenFromHeader(request.getHeader("Authorization"));
        String userId = jwtService.getUserIdFromToken(token);
        UserDTO user = userService.updateProfile(userId, userData);
        return ResponseEntity.ok(new ResponseDTO<>(200, null, "유저정보 변경 성공", user));
    }
    @DeleteMapping("/profile/{userId}")
    public ResponseEntity<String> deleteProfile(@PathVariable("userId") String userId){
        String result =userService.deleteProfile(userId);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/book")
    public ResponseEntity<ResponseDTO<?>> getBookingDetail(HttpServletRequest request){
        String token = jwtService.getTokenFromHeader(request.getHeader("Authorization"));
        String userId = jwtService.getUserIdFromToken(token);
        List<BookingDetailDTO> bookingDetail = bookingService.getBookingDetail(userId);
        return ResponseEntity.ok(new ResponseDTO<>(200, null, "예약 조회 성공", bookingDetail));
    }
    @PutMapping("/book")
    public ResponseEntity<ResponseDTO<?>> updateBooking(@RequestBody BookingDetailDTO bookingData) {
        BookingDetailDTO updatedBooking = bookingService.updateBooking(bookingData);

        return ResponseEntity.ok(new ResponseDTO<>(200, null, "예약 변경 성공", updatedBooking));
    }
    @DeleteMapping("/book/{bookingId}")
    public ResponseEntity<ResponseDTO<?>> deleteBooking(@PathVariable("bookingId") Long bookingId, HttpServletRequest request) {
        String token = jwtService.getTokenFromHeader(request.getHeader("Authorization"));
        String userId = jwtService.getUserIdFromToken(token);

        List<BookingDetailDTO> deleteBooking = bookingService.deleteBooking(userId, bookingId);

        return ResponseEntity.ok(new ResponseDTO<>(200, null, "예약 취소 성공", deleteBooking));
    }
    @PostMapping("/password-reset")
    public ResponseEntity<String> passWordReset(HttpServletRequest request, @RequestBody UserDTO user) {
        String token = jwtService.getTokenFromHeader(request.getHeader("Authorization"));
        String userId = jwtService.getUserIdFromToken(token);
        String newPassword = user.getPassword();
        String result =userService.passWordReset(userId, newPassword);
        return ResponseEntity.ok(result);

    }
    @PostMapping("/likes")
    public ResponseEntity<String> toggleLike(HttpServletRequest request,
                                             @RequestParam Long accommodationId) {
        String token = jwtService.getTokenFromHeader(request.getHeader("Authorization"));
        String userId = jwtService.getUserIdFromToken(token);
        String result = userService.toggleLike(userId, accommodationId);
        return ResponseEntity.ok(result);
    }
}
