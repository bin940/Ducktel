package com.ducktel.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "accommodation")
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accommodation_id")
    private Long accommodationId;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "accommodation_name")
    private String accommodationName;

    @Column(name = "category")
    private String category;

    @Column(name = "explanation")
    private String explanation;

    @Column(name = "location")
    private String location;

    @Column(name = "service_info")
    private String serviceInfo;

    @Column(name = "tag")
    private String tag;

    @Column(name = "season")
    private String season;

    @Column(name = "discount")

    private Integer discount;

    @Column(name = "like_count")
    private int likeCount;
}
