package com.ducktel.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    private String userId;
    private String token;
    private LocalDateTime expiryDate;
}