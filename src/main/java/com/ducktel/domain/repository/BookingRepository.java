package com.ducktel.domain.repository;


import com.ducktel.domain.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser_UserId(Long userId);
    @Query("SELECT b.room.roomId, COUNT(b) " +
            "FROM Booking b " +
            "WHERE b.room.roomId IN :roomIds " +
            "AND (b.checkIn <= :checkOut AND b. checkOut>= :checkIn) " +
            "GROUP BY b.room.roomId")
    List<Object[]> findBookedRooms(@Param("roomIds") List<Long> roomIds,
                                   @Param("checkIn") LocalDate startDate,
                                   @Param("checkOut") LocalDate endDate);
}
