package com.ducktel.domain.repository;


import com.ducktel.domain.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByAccommodation_AccommodationId(Long accommodationId);
}
