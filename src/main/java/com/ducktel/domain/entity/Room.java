package com.ducktel.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "accommodation_room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "checkin_explation")
    private String checkinExplanation;

    @Column(name = "explanation")
    private String explanation;

    @Column(name = "max_capacity", nullable = false)
    private int maxCapacity;

    @Column(name = "min_capacity", nullable = false)
    private int minCapacity;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "service_info")
    private String serviceInfo;

    @Column(name = "tag")
    private String tag;

    @Column(name = "total_rooms", nullable = false)
    private int totalRooms;

    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodationId;
}