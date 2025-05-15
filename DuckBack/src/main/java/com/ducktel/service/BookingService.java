package com.ducktel.service;

import com.ducktel.dto.BookingDetailDTO;

import java.util.List;
import java.util.UUID;

public interface BookingService {
    List<BookingDetailDTO> getBookingDetail(UUID userId);
    BookingDetailDTO updateBooking(BookingDetailDTO bookingData);
    List<BookingDetailDTO> deleteBooking(UUID userId,Long bookingId);
}
