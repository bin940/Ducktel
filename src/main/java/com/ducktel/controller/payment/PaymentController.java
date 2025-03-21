package com.ducktel.controller.payment;

import com.ducktel.config.security.jwt.JwtUtils;
import com.ducktel.dto.PaymentRequestDTO;
import com.ducktel.dto.PaymentResponseDTO;
import com.ducktel.dto.ResponseDTO;
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

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(HttpServletRequest request,@Valid  @RequestBody PaymentRequestDTO paymentRequestDTO) {
        String token = JwtUtils.getTokenFromHeader(request.getHeader("Authorization"));
        String userId = JwtUtils.getUserIdFromToken(token);
        paymentRequestDTO.setUserId(userId);
        PaymentResponseDTO paymentRetrieve =  paymentService.processPayment(paymentRequestDTO);
        return ResponseEntity.ok(new ResponseDTO<>(200, null, "예약 성공", paymentRetrieve));

    }
}
