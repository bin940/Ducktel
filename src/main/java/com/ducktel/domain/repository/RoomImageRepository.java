package com.ducktel.domain.repository;


import com.ducktel.domain.entity.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomImageRepository extends JpaRepository<RoomImage,Long> {
    List<RoomImage> findByRoom_RoomId(Long roomId);
    List<RoomImage> findByRoom_RoomIdIn(List<Long> roomIds);
}
