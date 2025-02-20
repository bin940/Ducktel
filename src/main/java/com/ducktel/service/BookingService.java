package com.ducktel.service;

import com.ducktel.dto.BookingDetailDTO;

import java.util.List;

public interface BookingService {
    List<BookingDetailDTO> getBookingDetail(Long userId);
    BookingDetailDTO updateBooking(BookingDetailDTO bookingData);
}
