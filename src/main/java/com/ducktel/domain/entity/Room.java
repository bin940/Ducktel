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

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private LocalDateTime updatedAt;

    @Column(name = "checkin_explation")
    private String checkinExplanation;

    private String explanation;
    private int maxCapacity;
    private int minCapacity;
    private String name;
    private int price;

    @Column(name = "service_info")
    private String serviceInfo;

    private String tag;

    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;
}