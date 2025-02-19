package com.ducktel.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "accommodation_room_image")
public class RoomImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    private String image;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}
