package com.ducktel.controller.payment;

import com.ducktel.dto.PaymentRequestDTO;
import com.ducktel.dto.PaymentResponseDTO;
import com.ducktel.dto.ResponseDTO;
import com.ducktel.service.JwtService;
import com.ducktel.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final JwtService jwtService;

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(HttpServletRequest request,@Valid  @RequestBody PaymentRequestDTO paymentRequestDTO) {
        String token = jwtService.getTokenFromHeader(request.getHeader("Authorization"));
        String userId = jwtService.getUserIdFromToken(token);

        if (userId == null) {
            return ResponseEntity.ok(new ResponseDTO<>(400, "AUTH_MISSING", "Authorization 헤더가 누락되었거나 유효하지 않습니다.", null));
        }
        paymentRequestDTO.setUserId(userId);
        PaymentResponseDTO paymentRetrieve =  paymentService.processPayment(paymentRequestDTO);
        return ResponseEntity.ok(new ResponseDTO<>(200, null, "예약 성공", paymentRetrieve));

    }
}
