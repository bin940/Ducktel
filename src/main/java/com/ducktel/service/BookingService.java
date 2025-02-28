package com.ducktel.service;

import com.ducktel.dto.BookingDetailDTO;

import java.util.List;

public interface BookingService {
    List<BookingDetailDTO> getBookingDetail(String userId);
    BookingDetailDTO updateBooking(BookingDetailDTO bookingData);
    List<BookingDetailDTO> deleteBooking(String userId,Long bookingId);
}
