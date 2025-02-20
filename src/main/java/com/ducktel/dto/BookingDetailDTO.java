package com.ducktel.dto;

import com.ducktel.domain.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
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


    public Booking updateBooikng(Booking booking) {
        return booking.toBuilder()
                .startDate(this.startDate)
                .endDate(this.endDate)
                .numberOfPersons(this.numberOfPerson)
                .build();
    }

}
