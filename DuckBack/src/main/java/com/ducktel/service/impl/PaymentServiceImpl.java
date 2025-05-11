package com.ducktel.service.impl;

import com.ducktel.domain.entity.*;
import com.ducktel.domain.enums.PaymentStatus;
import com.ducktel.domain.repository.*;
import com.ducktel.dto.PaymentRequestDTO;
import com.ducktel.dto.PaymentResponseDTO;
import com.ducktel.exception.CustomException;
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
    private final BookingRepository bookingRepository;
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

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setAccommodation(accommodation);
        booking.setRoom(room);
        booking.setName(paymentRequestDTO.getName());
        booking.setPhoneNumber(paymentRequestDTO.getPhoneNumber());
        booking.setCheckIn(paymentRequestDTO.getCheckInDate());
        booking.setCheckOut(paymentRequestDTO.getCheckOutDate());
        booking.setPaymentCompleted(false);
        bookingRepository.save(booking);
        log.info("예약 생성 성공: bookingId={}", booking.getBookingId());

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

            booking.setPaymentCompleted(true);
            bookingRepository.save(booking);
            log.info("결제 성공 처리 완료: paymentId={}, bookingId={}", payment.getPaymentId(), booking.getBookingId());

            return new PaymentResponseDTO("SUCCESS", payment.getPgTransactionId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            log.warn("결제 실패 처리 완료: paymentId={}", payment.getPaymentId());

            return new PaymentResponseDTO("FAILED", null);
        }
    }
}
