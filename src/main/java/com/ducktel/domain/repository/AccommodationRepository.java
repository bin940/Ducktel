package com.ducktel.domain.repository;

import com.ducktel.domain.entity.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    List<Accommodation> findByDiscountGreaterThan(Integer discount);
    List<Accommodation> findAllByOrderByLikeCountDesc();
    List<Accommodation> findBySeason(String season);
}
