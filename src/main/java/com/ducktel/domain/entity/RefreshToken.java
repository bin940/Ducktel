package com.ducktel.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class RefreshToken {
    @Id
    private String userId;
    private String token;
    private LocalDateTime expiryDate;
}