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
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "checkin_explation")
    private String checkinExplanation;
    private String explanation;
    @Column(name = "max_capacity")
    private int maxCapacity;
    @Column(name = "min_capacity")
    private int minCapacity;
    private String name;
    private int price;

    @Column(name = "service_info")
    private String serviceInfo;

    private String tag;
    @Column(name = "total_rooms")
    private int totalRooms;

    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodationId;
}