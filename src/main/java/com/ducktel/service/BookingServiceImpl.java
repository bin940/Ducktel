package com.ducktel.service;

import com.ducktel.domain.entity.Booking;
import com.ducktel.domain.entity.Room;
import com.ducktel.domain.entity.RoomImage;
import com.ducktel.domain.repository.BookingRepository;
import com.ducktel.domain.repository.RoomImageRepository;
import com.ducktel.domain.repository.RoomRepository;
import com.ducktel.dto.BookingDetailDTO;
import com.ducktel.dto.RoomDTO;
import com.ducktel.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;


    @Override
    public List<BookingDetailDTO> getBookingDetail(Long userId) {
        List<Booking> bookings = bookingRepository.findByUser_UserId(userId);
        return convertToBookingDetailDTOList(bookings);
    }

    @Override
    public BookingDetailDTO updateBooking(BookingDetailDTO bookingData) {
        Long bookingId = bookingData.getBookingId();
        log.info("bookingId: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException("NOT FOUND","예약을 찾을 수 없습니다. ID: " + bookingId));
        log.info("booking: {}", booking);
        booking = bookingData.updateBooikng(booking);
        Booking updatedBooking = bookingRepository.save(booking);
        return updatedBooking.updateBooking(bookingData);
    }

    private List<BookingDetailDTO> convertToBookingDetailDTOList(List<Booking> bookings) {
        return bookings.stream().map(booking -> {
            Room room = roomRepository.findById(booking.getRoom().getRoomId())
                    .orElseThrow(() -> new CustomException("NOT FOUND", "객실 정보를 찾을 수 없습니다."));

            List<String> images = roomImageRepository.findByRoom_RoomId(room.getRoomId())
                    .stream()
                    .map(RoomImage::getImage)
                    .toList();

            return BookingDetailDTO.builder()
                    .bookingId(booking.getBookingId())
                    .createdAt(booking.getCreatedAt())
                    .startDate(booking.getStartDate())
                    .endDate(booking.getEndDate())
                    .numberOfPerson(booking.getNumberOfPersons())
                    .paymentCompleted(booking.isPaymentCompleted())
                    .room(RoomDTO.builder()
                            .roomId(room.getRoomId())
                            .name(room.getName())
                            .maxCapacity(room.getMaxCapacity())
                            .minCapacity(room.getMinCapacity())
                            .price(room.getPrice())
                            .explanation(room.getExplanation())
                            .serviceInfo(room.getServiceInfo())
                            .tag(room.getTag())
                            .images(images)
                            .build())
                    .build();
        }).collect(Collectors.toList());
    }
}

