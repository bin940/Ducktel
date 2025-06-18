package com.ducktel.kafka;

import com.ducktel.dto.PaymentRequestDTO;
import com.ducktel.enums.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, PaymentRequestDTO> kafkaTemplate;

    public void sendPayment(PaymentRequestDTO dto) {
        log.info("Kafka로 결제 요청 전송: {}", dto);
        kafkaTemplate.send(KafkaTopic.PAYMENT.getTopic(), dto.getUserId().toString(), dto)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka 전송 실패!", ex);
                    } else {
                        log.info("Kafka 전송 성공: {}", result.getRecordMetadata());
                    }
                });

    }
}
