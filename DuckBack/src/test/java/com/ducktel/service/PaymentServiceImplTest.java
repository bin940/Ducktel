package com.ducktel.service;

import com.ducktel.domain.entity.Accommodation;
import com.ducktel.domain.entity.Room;
import com.ducktel.domain.entity.User;
import com.ducktel.domain.repository.*;
import com.ducktel.dto.PaymentRequestDTO;
import com.ducktel.dto.PaymentResponseDTO;
import com.ducktel.exception.CustomException;
import com.ducktel.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private UserRepository userRepository;
    @Mock private AccommodationRepository accommodationRepository;
    @Mock private RoomRepository roomRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequestDTO requestDTO;
    private User user;
    private Accommodation accommodation;
    private Room room;

    @BeforeEach
    void setUp() {
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");
        requestDTO = new PaymentRequestDTO();
        requestDTO.setUserId(uuid);
        requestDTO.setAccommodationId(1L);
        requestDTO.setRoomId(2L);
        requestDTO.setName("홍길동");
        requestDTO.setPhoneNumber("01012345678");
        requestDTO.setCheckInDate(LocalDate.now());
        requestDTO.setCheckOutDate(LocalDate.now().plusDays(1));
        requestDTO.setAmount(BigDecimal.valueOf(100000));
        requestDTO.setPaymentMethod("CARD");

        user = new User();
        user.setUserId(uuid);

        room = new Room();
        room.setRoomId(2L);

        accommodation = new Accommodation();
        accommodation.setAccommodationId(1L);
    }

    @Test
    void processPayment_SuccessOrFailed() {
        mockRepositories();
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponseDTO response = paymentService.processPayment(requestDTO);

        assertThat(response.getMessage()).isIn("SUCCESS", "FAILED");
    }

    @Test
    void processPayment_UserNotFound() {
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");
        when(userRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.processPayment(requestDTO))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("아이디를 찾을 수 없습니다");
    }

    @Test
    void processPayment_AccommodationNotFound() {
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");
        when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
        when(accommodationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.processPayment(requestDTO))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("숙소를 찾을 수 없습니다");
    }

    @Test
    void processPayment_RoomNotFound() {
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");
        when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(roomRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.processPayment(requestDTO))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("객실을 찾을 수 없습니다");
    }

    private void mockRepositories() {
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");
        when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(roomRepository.findById(2L)).thenReturn(Optional.of(room));
    }
}
