package com.ducktel.kafka;

import com.ducktel.dto.PaymentRequestDTO;
import com.ducktel.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "payment-topic", groupId = "payment-group")
    public void consume(PaymentRequestDTO dto) {
        log.info("Kafka 메시지 수신: {}", dto);

        try {
            paymentService.processPayment(dto); // 기존 서비스 호출
        } catch (Exception e) {
            log.error("Kafka 처리 중 오류 발생", e);
        }
    }
}
