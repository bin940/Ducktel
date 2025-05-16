package com.ducktel.service.impl;

import com.ducktel.domain.entity.Payment;
import com.ducktel.domain.enums.PaymentStatus;
import com.ducktel.domain.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class PaymentCleanupService {

    private final PaymentRepository paymentRepository;

    public PaymentCleanupService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Scheduled(fixedRate = 10 * 60 * 1000) // 10분 주기
    public void expireOldPendingPayments() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(15);
        List<Payment> expiredPayments = paymentRepository.findByStatusAndCreatedAtBefore(PaymentStatus.PENDING, threshold);

        for (Payment payment : expiredPayments) {
            payment.setStatus(PaymentStatus.EXPIRED);
        }

        paymentRepository.saveAll(expiredPayments);
        log.info("만료된 결제 {}건 처리 완료", expiredPayments.size());
    }
}
