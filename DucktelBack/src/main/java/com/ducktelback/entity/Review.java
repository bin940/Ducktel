package com.ducktelback.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private int reviewId;

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private LocalDateTime updatedAt;

    private String image;

    @Column(name = "review_content")
    private String reviewContent;

    private int star;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservations reservation;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private AccommodationRoom room;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;
}
