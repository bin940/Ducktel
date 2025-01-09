package com.ducktelback.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reservations")
public class Reservations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private int reservationId;

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private LocalDateTime updatedAt;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "number_of_persor")
    private int numberOfPersons;

    @Column(name = "payment_completed")
    private boolean paymentCompleted;

    @Column(name = "start_date")
    private LocalDate startDate;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private AccommodationRoom room;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
