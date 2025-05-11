package com.ducktel.controller.book;

import com.ducktel.dto.BookingDetailDTO;
import com.ducktel.dto.ResponseDTO;
import com.ducktel.service.BookingService;
import com.ducktel.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class BookController{

    private final BookingService bookingService;
    private final JwtService jwtService;

    @GetMapping("/book")
    public ResponseEntity<ResponseDTO<?>> getBookingDetail(HttpServletRequest request) {
        log.debug("Authorization 헤더에서 토큰 추출 시도");
        String token = jwtService.getTokenFromHeader(request.getHeader("Authorization"));
        String userId = jwtService.getUserIdFromToken(token);
        log.info("토큰에서 추출한 사용자 ID: {}", userId);

        List<BookingDetailDTO> bookingDetail = bookingService.getBookingDetail(userId);
        log.info("예약 조회 성공: {}", bookingDetail);

        return ResponseEntity.ok(new ResponseDTO<>(200, null, "예약 조회 성공", bookingDetail));
    }

    @PutMapping("/book")
    public ResponseEntity<ResponseDTO<?>> updateBooking(@RequestBody BookingDetailDTO bookingData) {
        log.info("예약 변경 요청 데이터: {}", bookingData);

        BookingDetailDTO updatedBooking = bookingService.updateBooking(bookingData);
        log.info("예약 변경 성공: {}", updatedBooking);

        return ResponseEntity.ok(new ResponseDTO<>(200, null, "예약 변경 성공", updatedBooking));
    }

    @DeleteMapping("/book/{bookingId}")
    public ResponseEntity<ResponseDTO<?>> deleteBooking(@PathVariable("bookingId") Long bookingId, HttpServletRequest request) {
        log.debug("Authorization 헤더에서 토큰 추출 시도");
        String token = jwtService.getTokenFromHeader(request.getHeader("Authorization"));
        String userId = jwtService.getUserIdFromToken(token);
        log.info("토큰에서 추출한 사용자 ID: {}", userId);
        log.info("예약 취소 요청 ID: {}", bookingId);

        List<BookingDetailDTO> deleteBooking = bookingService.deleteBooking(userId, bookingId);
        log.info("예약 취소 성공: {}", deleteBooking);

        return ResponseEntity.ok(new ResponseDTO<>(200, null, "예약 취소 성공", deleteBooking));
    }


}
