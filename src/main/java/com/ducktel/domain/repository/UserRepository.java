package com.ducktel.domain.repository;

import com.ducktel.domain.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<User> findBySocialId(@Param("socialId") String socialId);
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.userId = :userId")
    int updatePassword(@Param("userId") String userId, @Param("password") String newPassword);
}
