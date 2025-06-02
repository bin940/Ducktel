package com.ducktel.kafka;

import com.ducktel.dto.PaymentRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, PaymentRequestDTO> kafkaTemplate;
    private final String TOPIC = "payment-topic";

    public KafkaProducer(KafkaTemplate<String, PaymentRequestDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPayment(PaymentRequestDTO dto) {
        log.info("Kafka로 결제 요청 전송: {}", dto);
        kafkaTemplate.send(TOPIC, dto.getUserId().toString(), dto)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka 전송 실패!", ex);
                    } else {
                        log.info("Kafka 전송 성공: {}", result.getRecordMetadata());
                    }
                });

    }
}