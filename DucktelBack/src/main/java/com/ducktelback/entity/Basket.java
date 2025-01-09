package com.ducktelback.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "basket")
public class Basket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "basket_id")
    private int basketId;

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservations reservation;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private AccommodationRoom room;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_selected")
    private boolean isSelected;
}
