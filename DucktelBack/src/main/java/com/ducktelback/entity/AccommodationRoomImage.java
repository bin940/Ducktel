package com.ducktelback.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "accommodation_room_image")
public class AccommodationRoomImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private int imageId;

    private String image;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private AccommodationRoom room;
}
