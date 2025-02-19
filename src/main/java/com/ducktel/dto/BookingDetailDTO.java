package com.ducktel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailDTO {

    private Long bookingId;
    private LocalDateTime createdAt;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numberOfPerson;
    private boolean paymentCompleted;

    private AccommodationDTO accommodation;
    private RoomDTO room;


}
