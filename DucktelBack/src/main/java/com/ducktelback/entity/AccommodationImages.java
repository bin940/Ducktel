package com.ducktelback.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "accommodation_images")
public class AccommodationImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private int imageId;

    private String image;

    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;
}
