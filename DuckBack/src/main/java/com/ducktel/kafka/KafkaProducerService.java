package com.ducktel.kafka;

import com.ducktel.dto.PaymentRequestDTO;
import com.ducktel.enums.KafkaTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, PaymentRequestDTO> kafkaTemplate;

    public void sendPayment(PaymentRequestDTO paymentRequestDTO) {
        kafkaTemplate.send(KafkaTopic.PAYMENT.getTopic(), paymentRequestDTO);
    }
}
