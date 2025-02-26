package com.ducktel.domain.entity;

import com.ducktel.dto.BookingDetailDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @Column(name = "payment_completed", nullable = false)
    private boolean paymentCompleted;

    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "name" , nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

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
               .checkIn(this.checkIn)
               .checkOut(this.checkOut)
               .build();
    }
}
