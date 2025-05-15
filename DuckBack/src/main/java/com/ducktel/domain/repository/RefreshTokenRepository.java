package com.ducktel.domain.repository;

import com.ducktel.domain.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    @Transactional
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.token = :token, rt.expiryDate = :expiryDate WHERE rt.userId = :userId")
    void updateTokenByUserId(
            @Param("userId") UUID userId,
            @Param("token") String token,
            @Param("expiryDate") LocalDateTime expiryDate
    );
}
