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
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final AccommodationRepository accommodationRepository;
    private final RoomRepository roomRepository;

    @Transactional
    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequestDTO) {
        User user = userRepository.findById(paymentRequestDTO.getUserId())
                .orElseThrow(() -> new CustomException(404,"NOT FOUND", "아이디를 찾을 수 없습니다. ID: " + paymentRequestDTO.getUserId()));
        Accommodation accommodation = accommodationRepository.findById(paymentRequestDTO.getAccommodationId())
                .orElseThrow(() -> new CustomException(404,"NOT FOUND", "숙소를 찾을 수 없습니다." + paymentRequestDTO.getAccommodationId()));
        Room room = roomRepository.findById(paymentRequestDTO.getRoomId())
                .orElseThrow(() -> new CustomException(404,"NOT FOUND", "객실을 찾을 수 없습니다." + paymentRequestDTO.getRoomId()));

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

        Payment payment = new Payment();
        payment.setUserId(user);
        payment.setAmount(paymentRequestDTO.getAmount());
        payment.setPaymentMethod(paymentRequestDTO.getPaymentMethod());
        payment.setPgTransactionId(UUID.randomUUID().toString());
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        boolean isSuccess = new Random().nextBoolean();
        if (isSuccess) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPgTransactionId(UUID.randomUUID().toString());
            paymentRepository.save(payment);

            booking.setPaymentCompleted(true);
            bookingRepository.save(booking);

            return new PaymentResponseDTO("SUCCESS", payment.getPgTransactionId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);

            return new PaymentResponseDTO("FAILED", null);
        }
    }
}
