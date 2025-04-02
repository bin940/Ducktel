package com.ducktel.service;


import com.ducktel.domain.entity.*;
import com.ducktel.domain.repository.*;
import com.ducktel.dto.BookingDetailDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private RoomImageRepository roomImageRepository;
    @Mock private AccommodationRepository accommodationRepository;
    @Mock private AccommodationImageRepository accommodationImageRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking;
    private Room room;
    private Accommodation accommodation;

    @BeforeEach
    void setUp() {

        room = new Room();
        room.setRoomId(100L);
        room.setName("디럭스룸");
        room.setMaxCapacity(4);
        room.setMinCapacity(1);
        room.setPrice(150000);
        room.setExplanation("설명");
        room.setServiceInfo("서비스");
        room.setTag("오션뷰");

        accommodation = new Accommodation();
        accommodation.setAccommodationId(200L);
        accommodation.setAccommodationName("호텔덕텔");
        accommodation.setLocation("서울");
        accommodation.setTag("럭셔리");
        accommodation.setExplanation("고급 호텔");
        accommodation.setServiceInfo("무료 조식");
        accommodation.setDiscount(10);
        accommodation.setSeason("여름");
        accommodation.setLikeCount(99);
        accommodation.setCategory("호텔");

        booking = new Booking();
        booking.setBookingId(1L);
        booking.setCheckIn(LocalDate.of(2025, 4, 1));
        booking.setCheckOut(LocalDate.of(2025, 4, 3));
        booking.setCreatedAt(LocalDateTime.of(2025, 3, 20, 10, 0));
        booking.setRoom(room);
        booking.setAccommodation(accommodation);
        booking.setPaymentCompleted(true);
        booking.setName("홍길동");
        booking.setPhoneNumber("01012345678");
    }

    @Test
    void getBookingDetail_BookingDetailDTOList() {
        String userId = "user123";
        when(bookingRepository.findByUser_UserId(userId)).thenReturn(List.of(booking));
        when(roomRepository.findById(room.getRoomId())).thenReturn(Optional.of(room));

        RoomImage roomImage = new RoomImage();
        roomImage.setImage("room_img.jpg");
        roomImage.setRoom(room);
        when(roomImageRepository.findByRoom_RoomId(room.getRoomId())).thenReturn(List.of(roomImage));

        when(accommodationRepository.findById(accommodation.getAccommodationId())).thenReturn(Optional.of(accommodation));

        AccommodationImage accommodationImage = new AccommodationImage();
        accommodationImage.setImage("acc_img.jpg");
        accommodationImage.setAccommodation(accommodation);
        when(accommodationImageRepository.findByAccommodation_AccommodationId(accommodation.getAccommodationId())).thenReturn(List.of(accommodationImage));

        List<BookingDetailDTO> result = bookingService.getBookingDetail(userId);

        assertThat(result).hasSize(1);
        BookingDetailDTO dto = result.get(0);
        assertThat(dto.getBookingId()).isEqualTo(1L);
        assertThat(dto.getRoom().getName()).isEqualTo("디럭스룸");
        assertThat(dto.getAccommodation().getName()).isEqualTo("호텔덕텔");
        assertThat(dto.getRoom().getImages()).contains("room_img.jpg");
        assertThat(dto.getAccommodation().getImage()).contains("acc_img.jpg");
    }
}