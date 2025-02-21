package com.ducktel.domain.entity;

import com.ducktel.dto.BookingDetailDTO;
import com.ducktel.dto.RoomDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "booking")
@Builder(toBuilder = true)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private LocalDateTime updatedAt;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "number_of_person")
    private int numberOfPersons;

    @Column(name = "payment_completed")
    private boolean paymentCompleted;

    @Column(name = "start_date")
    private LocalDate startDate;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
    @ManyToOne
    @JoinColumn(name= "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public BookingDetailDTO updateBooking(BookingDetailDTO bookingDTO){
       return bookingDTO.toBuilder()
               .startDate(this.startDate)
               .endDate(this.endDate)
               .numberOfPerson(this.numberOfPersons)
               .build();
    }
}
