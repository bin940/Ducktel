package com.ducktel.domain.repository;

import com.ducktel.domain.entity.AccommodationImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccommodationImageRepository extends JpaRepository<AccommodationImage, Long> {
    List<AccommodationImage> findByAccommodation_AccommodationId(Long accommodationId);
}
