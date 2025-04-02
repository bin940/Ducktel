package com.ducktel.domain.repository;

import com.ducktel.domain.entity.Accommodation;
import com.ducktel.domain.entity.AccommodationLike;
import com.ducktel.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccommodationLikeRepository extends JpaRepository<AccommodationLike, Long> {
    Optional<AccommodationLike> findByUserAndAccommodation(User user, Accommodation accommodation);
}
