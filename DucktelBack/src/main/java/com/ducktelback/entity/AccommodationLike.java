package com.ducktelback.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "accommodation_like")
public class AccommodationLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_like")
    private boolean isLike;

    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
