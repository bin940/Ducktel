package com.ducktelback.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "review_images")
public class ReviewImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private int imageId;

    private String image;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;
}