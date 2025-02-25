package com.ducktel.service;

import com.ducktel.domain.entity.*;
import com.ducktel.domain.repository.*;
import com.ducktel.dto.AccommodationDTO;
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
    private  final AccommodationRepository accommodationRepository;
    private  final AccommodationImageRepository accommodationImageRepository;


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

    @Override
    public List<BookingDetailDTO> deleteBooking(Long userId, Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new CustomException("NOT FOUND", "예약을 찾을 수 없습니다. ID: " + bookingId);
        }

        bookingRepository.deleteById(bookingId);

        List<Booking> bookings = bookingRepository.findByUser_UserId(userId);

        return convertToBookingDetailDTOList(bookings);
    }

    private List<BookingDetailDTO> convertToBookingDetailDTOList(List<Booking> bookings) {
        return bookings.stream().map(booking -> {
            Room room = roomRepository.findById(booking.getRoom().getRoomId())
                    .orElseThrow(() -> new CustomException("NOT FOUND", "객실 정보를 찾을 수 없습니다."));

            List<String> roomImages = roomImageRepository.findByRoom_RoomId(room.getRoomId())
                    .stream()
                    .map(RoomImage::getImage)
                    .toList();

            Accommodation accommodation = accommodationRepository.findById(booking.getAccommodation().getAccommodationId())
                    .orElseThrow(() -> new CustomException("NOT FOUND", "숙소 정보를 찾을 수가 없습니다."));

            List<String> accommodationImages = accommodationImageRepository.findByAccommodation_AccommodationId(accommodation.getAccommodationId())
                    .stream()
                    .map(AccommodationImage::getImage)
                    .toList();

            return BookingDetailDTO.builder()
                    .bookingId(booking.getBookingId())
                    .createdAt(booking.getCreatedAt())
                    .checkIn(booking.getCheckIn())
                    .checkOut(booking.getCheckOut())
                    .numberOfPerson(booking.getNumberOfPersons())
                    .paymentCompleted(booking.isPaymentCompleted())
                    .accommodation(AccommodationDTO.builder()
                            .accommodationId(accommodation.getAccommodationId())
                            .name(accommodation.getAccommodationName())
                            .location(accommodation.getLocation())
                            .tag(accommodation.getTag())
                            .explanation(accommodation.getExplanation())
                            .serviceInfo(accommodation.getServiceInfo())
                            .image(accommodationImages)
                            .discount(accommodation.getDiscount())
                            .season(accommodation.getSeason())
                            .likeCount(accommodation.getLikeCount())
                            .category(accommodation.getCategory())
                            .build())
                    .room(RoomDTO.builder()
                            .roomId(room.getRoomId())
                            .name(room.getName())
                            .maxCapacity(room.getMaxCapacity())
                            .minCapacity(room.getMinCapacity())
                            .price(room.getPrice())
                            .explanation(room.getExplanation())
                            .serviceInfo(room.getServiceInfo())
                            .tag(room.getTag())
                            .images(roomImages)
                            .build())
                    .build();
        }).collect(Collectors.toList());
    }
}

