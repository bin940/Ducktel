package com.ducktel.controller.payment;

import com.ducktel.dto.PaymentRequestDTO;
import com.ducktel.dto.ResponseDTO;
import com.ducktel.kafka.KafkaProducer;
import com.ducktel.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@Slf4j
public class PaymentController {

    private final KafkaProducer kafkaProducer;
    private final JwtService jwtService;

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(HttpServletRequest request,@Valid  @RequestBody PaymentRequestDTO paymentRequestDTO) {
        log.debug("결제 생성 요청 데이터: {}", paymentRequestDTO);

        String token = jwtService.getTokenFromHeader(request.getHeader("Authorization"));
        log.debug("Authorization 헤더에서 추출한 토큰: {}", token);

        UUID userId = jwtService.getUserIdFromToken(token);
        log.info("토큰에서 추출한 사용자 ID: {}", userId);

        if (userId == null) {
            log.warn("유효하지 않은 Authorization 헤더로 요청: token={}", token);
            return ResponseEntity.ok(new ResponseDTO<>(400, "AUTH_MISSING", "Authorization 헤더가 누락되었거나 유효하지 않습니다.", null));
        }
        paymentRequestDTO.setUserId(userId);
        log.debug("결제 요청 데이터에 사용자 ID 설정 완료: {}", paymentRequestDTO);

        kafkaProducer.sendPayment(paymentRequestDTO);

        return ResponseEntity.ok(new ResponseDTO<>(200, null, "결제 요청을 Kafka로 전송 완료", null));

    }
}
