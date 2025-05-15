package com.ducktel.service;

import com.ducktel.domain.entity.Booking;
import com.ducktel.dto.BookingDetailDTO;
import com.ducktel.dto.PaymentRequestDTO;

import java.util.List;
import java.util.UUID;

public interface BookingService {
    List<BookingDetailDTO> getBookingDetail(UUID userId);
    BookingDetailDTO updateBooking(BookingDetailDTO bookingData);
    List<BookingDetailDTO> deleteBooking(UUID userId,Long bookingId);
    Booking createBooking(PaymentRequestDTO dto);
}
