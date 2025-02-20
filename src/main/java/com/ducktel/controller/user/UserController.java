package com.ducktel.controller.user;

import com.ducktel.config.security.jwt.JwtUtils;
import com.ducktel.dto.BookingDetailDTO;
import com.ducktel.dto.UserDTO;
import com.ducktel.service.BookingService;
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
    @DeleteMapping("/profile/{userId}")
    public ResponseEntity<String> deleteProfile(@PathVariable("userId") Long userId){
        String result =userService.deleteProfile(userId);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/book")
    public ResponseEntity<List<BookingDetailDTO>> getBookingDetail(HttpServletRequest request){
        String token = JwtUtils.getTokenFromHeader(request.getHeader("Authorization"));
        Long userId = JwtUtils.getUserIdFromToken(token);
        List<BookingDetailDTO> bookingDetail = bookingService.getBookingDetail(userId);
        return ResponseEntity.ok(bookingDetail);
    }
    @PutMapping("/book")
    public ResponseEntity<BookingDetailDTO> updateBooking(@RequestBody BookingDetailDTO bookingData) {
        BookingDetailDTO updatedBooking = bookingService.updateBooking(bookingData);

        return ResponseEntity.ok(updatedBooking);
    }
    @DeleteMapping("/book/{bookingId}")
    public ResponseEntity<List<BookingDetailDTO>> deleteBooking(@PathVariable("bookingId") Long bookingId, HttpServletRequest request) {
        String token = JwtUtils.getTokenFromHeader(request.getHeader("Authorization"));
        Long userId = JwtUtils.getUserIdFromToken(token);

        List<BookingDetailDTO> deleteBooking = bookingService.deleteBooking(userId, bookingId);

        return ResponseEntity.ok(deleteBooking);
    }
    @PostMapping("/password-reset")
    public ResponseEntity<String> passWordReset(HttpServletRequest request, @RequestBody UserDTO user) {
        String token = JwtUtils.getTokenFromHeader(request.getHeader("Authorization"));
        Long userId = JwtUtils.getUserIdFromToken(token);
        String newPassword = user.getPassword();
        String result =userService.passWordReset(userId, newPassword);
        return ResponseEntity.ok(result);

    }
}
