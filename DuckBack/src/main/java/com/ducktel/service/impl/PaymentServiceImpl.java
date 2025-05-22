package com.ducktel.service.impl;

import com.ducktel.domain.entity.Accommodation;
import com.ducktel.domain.entity.Payment;
import com.ducktel.domain.entity.Room;
import com.ducktel.domain.entity.User;
import com.ducktel.enums.PaymentStatus;
import com.ducktel.domain.repository.*;
import com.ducktel.dto.PaymentRequestDTO;
import com.ducktel.dto.PaymentResponseDTO;
import com.ducktel.exception.CustomException;
import com.ducktel.service.BookingService;
import com.ducktel.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final AccommodationRepository accommodationRepository;
    private final RoomRepository roomRepository;

    @Transactional
    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequestDTO) {
        log.debug("결제 처리 요청 데이터: {}", paymentRequestDTO);

        User user = userRepository.findById(paymentRequestDTO.getUserId())
                .orElseThrow(() -> {
                    log.warn("사용자 조회 실패: userId={}", paymentRequestDTO.getUserId());
                    return new CustomException(404, "NOT FOUND", "아이디를 찾을 수 없습니다. ID: " + paymentRequestDTO.getUserId());
                });
        log.debug("사용자 조회 성공: userId={}", user.getUserId());

        Accommodation accommodation = accommodationRepository.findById(paymentRequestDTO.getAccommodationId())
                .orElseThrow(() -> {
                    log.warn("숙소 조회 실패: accommodationId={}", paymentRequestDTO.getAccommodationId());
                    return new CustomException(404, "NOT FOUND", "숙소를 찾을 수 없습니다. ID: " + paymentRequestDTO.getAccommodationId());
                });
        log.debug("숙소 조회 성공: accommodationId={}", accommodation.getAccommodationId());

        Room room = roomRepository.findById(paymentRequestDTO.getRoomId())
                .orElseThrow(() -> {
                    log.warn("객실 조회 실패: roomId={}", paymentRequestDTO.getRoomId());
                    return new CustomException(404, "NOT FOUND", "객실을 찾을 수 없습니다. ID: " + paymentRequestDTO.getRoomId());
                });
        log.debug("객실 조회 성공: roomId={}", room.getRoomId());

        Payment payment = new Payment();
        payment.setUserId(user);
        payment.setAmount(paymentRequestDTO.getAmount());
        payment.setPaymentMethod(paymentRequestDTO.getPaymentMethod());
        payment.setPgTransactionId(UUID.randomUUID().toString());
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);
        log.info("결제 생성 성공: paymentId={}, status={}", payment.getPaymentId(), payment.getStatus());

        boolean isSuccess = new Random().nextBoolean();

        if (isSuccess) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPgTransactionId(UUID.randomUUID().toString());
            paymentRepository.save(payment);

            bookingService.createBooking(paymentRequestDTO);


            log.info("예약 생성 및 결제 성공 처리 완료: paymentId={}", payment.getPaymentId());
            return new PaymentResponseDTO("SUCCESS", payment.getPgTransactionId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            log.warn("결제 실패 처리 완료: paymentId={}", payment.getPaymentId());
            throw new CustomException(500, "PAYMENT_FAILED", "결제에 실패했습니다. 다시 시도해주세요.");
        }
    }
}
