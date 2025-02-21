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
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private LocalDateTime updatedAt;
    @Column(name = "accommodation_name")
    private String accommodationName;
    private String category;
    private String explanation;
    private String location;
    @Column(name = "service_info")
    private String serviceInfo;
    private String tag;
    private String season;
    private Integer discount;
}
