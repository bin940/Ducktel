package com.ducktel.domain.repository;


import com.ducktel.domain.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser_UserId(UUID userId);
    @Query("SELECT b.room.roomId, COUNT(b) " +
            "FROM Booking b " +
            "WHERE b.room.roomId IN :roomIds " +
            "AND (b.checkIn <= :checkOut AND b. checkOut>= :checkIn) " +
            "GROUP BY b.room.roomId")

    List<Object[]> findBookedRooms(@Param("roomIds") List<Long> roomIds,
                                   @Param("checkIn") LocalDate startDate,
                                   @Param("checkOut") LocalDate endDate);

    @Query("SELECT COUNT(b) FROM Booking b " +
            "WHERE b.room.roomId = :roomId " +
            "AND (b.checkIn < :checkOut AND b.checkOut > :checkIn)")
    long countByRoomAndDateRange(@Param("roomId") Long roomId,
                                 @Param("checkIn") LocalDate checkIn,
                                 @Param("checkOut") LocalDate checkOut);
}
