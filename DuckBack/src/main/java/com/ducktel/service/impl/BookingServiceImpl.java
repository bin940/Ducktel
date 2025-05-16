package com.ducktel.service.impl;

import com.ducktel.domain.entity.*;
import com.ducktel.domain.repository.*;
import com.ducktel.dto.AccommodationDTO;
import com.ducktel.dto.BookingDetailDTO;
import com.ducktel.dto.PaymentRequestDTO;
import com.ducktel.dto.RoomDTO;
import com.ducktel.exception.CustomException;
import com.ducktel.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomImageRepository roomImageRepository;
    private  final AccommodationRepository accommodationRepository;
    private  final AccommodationImageRepository accommodationImageRepository;


    @Override
    public List<BookingDetailDTO> getBookingDetail(UUID userId) {
        log.debug("BookingDetail 조회 요청: userId={}", userId);

        List<Booking> bookings = bookingRepository.findByUser_UserId(userId);
        List<BookingDetailDTO> bookingDetails = convertToBookingDetailDTOList(bookings);

        log.info("BookingDetail 조회 성공: userId={}, bookingCount={}", userId, bookingDetails.size());
        return bookingDetails;
    }

    @Override
    public BookingDetailDTO updateBooking(BookingDetailDTO bookingData) {
        Long bookingId = bookingData.getBookingId();
        log.debug("Booking 업데이트 요청: bookingId={}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Booking 업데이트 실패 - 예약을 찾을 수 없음: bookingId={}", bookingId);
                    return new CustomException(404, "NOT FOUND", "예약을 찾을 수 없습니다. ID: " + bookingId);
                });

        log.info("Booking 업데이트 진행: bookingId={}", bookingId);
        booking = bookingData.updateBooikng(booking);
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Booking 업데이트 성공: bookingId={}", bookingId);
        return updatedBooking.updateBooking(bookingData);
    }

    @Override
    public List<BookingDetailDTO> deleteBooking(UUID userId, Long bookingId) {
        log.debug("Booking 삭제 요청: userId={}, bookingId={}", userId, bookingId);

        if (!bookingRepository.existsById(bookingId)) {
            log.warn("Booking 삭제 실패 - 예약을 찾을 수 없음: bookingId={}", bookingId);
            throw new CustomException(404, "NOT FOUND", "예약을 찾을 수 없습니다. ID: " + bookingId);
        }

        bookingRepository.deleteById(bookingId);
        log.info("Booking 삭제 성공: bookingId={}", bookingId);

        List<Booking> bookings = bookingRepository.findByUser_UserId(userId);
        List<BookingDetailDTO> bookingDetails = convertToBookingDetailDTOList(bookings);

        log.info("Booking 삭제 후 남은 예약 조회 성공: userId={}, remainingBookingCount={}", userId, bookingDetails.size());
        return bookingDetails;
    }

    @Override
    public Booking createBooking(PaymentRequestDTO dto) {

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new CustomException(404, "NOT_FOUND", "객실 정보를 찾을 수 없습니다. ID: " + dto.getRoomId()));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new CustomException(404, "NOT_FOUND", "사용자 정보를 찾을 수 없습니다. ID: " + dto.getUserId()));

        long reservedCount = bookingRepository.countByRoomAndDateRange(
                room.getRoomId(),
                dto.getCheckInDate(),
                dto.getCheckOutDate()
        );

        if (reservedCount >= room.getStock(reservedCount)) {
            throw new CustomException(409, "FULLY_BOOKED", "해당 날짜에는 예약 가능한 객실이 없습니다.");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setAccommodation(room.getAccommodationId());
        booking.setCheckIn(dto.getCheckInDate());
        booking.setCheckOut(dto.getCheckOutDate());
        booking.setName(dto.getName());
        booking.setPhoneNumber(dto.getPhoneNumber());
        booking.setPaymentCompleted(true);

        return bookingRepository.save(booking);
    }

    private List<BookingDetailDTO> convertToBookingDetailDTOList(List<Booking> bookings) {
        log.debug("BookingDetail 변환 요청: bookingCount={}", bookings.size());

        List<BookingDetailDTO> bookingDetails = bookings.stream().map(booking -> {
            Room room = roomRepository.findById(booking.getRoom().getRoomId())
                    .orElseThrow(() -> {
                        log.warn("Room 조회 실패: roomId={}", booking.getRoom().getRoomId());
                        return new CustomException(404, "NOT FOUND", "객실 정보를 찾을 수 없습니다.");
                    });

            List<String> roomImages = roomImageRepository.findByRoom_RoomId(room.getRoomId())
                    .stream()
                    .map(RoomImage::getImage)
                    .toList();

            Accommodation accommodation = accommodationRepository.findById(booking.getAccommodation().getAccommodationId())
                    .orElseThrow(() -> {
                        log.warn("Accommodation 조회 실패: accommodationId={}", booking.getAccommodation().getAccommodationId());
                        return new CustomException(404, "NOT FOUND", "숙소 정보를 찾을 수가 없습니다.");
                    });

            List<String> accommodationImages = accommodationImageRepository.findByAccommodation_AccommodationId(accommodation.getAccommodationId())
                    .stream()
                    .map(AccommodationImage::getImage)
                    .toList();

            log.debug("BookingDetail 변환 성공: bookingId={}", booking.getBookingId());
            return BookingDetailDTO.builder()
                    .bookingId(booking.getBookingId())
                    .createdAt(booking.getCreatedAt())
                    .checkIn(booking.getCheckIn())
                    .checkOut(booking.getCheckOut())
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

        log.info("BookingDetail 변환 완료: bookingCount={}", bookingDetails.size());
        return bookingDetails;
    }
}

