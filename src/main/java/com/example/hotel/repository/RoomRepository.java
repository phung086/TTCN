package com.example.hotel.repository;

import com.example.hotel.domain.entity.Room;
import com.example.hotel.domain.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    long countByStatus(RoomStatus status);

    @Query("SELECT r FROM Room r WHERE r.status != 'OUT_OF_SERVICE' AND r.id NOT IN (" +
            "  SELECT rr.room.id FROM ReservationRoom rr " +
            "  JOIN rr.reservation res " +
            "  WHERE res.status NOT IN ('CANCELED', 'CHECKED_OUT') " +
            "  AND (res.checkInDate < :checkOut AND res.checkOutDate > :checkIn)" +
            ") AND (:roomTypeId IS NULL OR r.roomType.id = :roomTypeId)")
    List<Room> findAvailableRooms(@Param("checkIn") LocalDate checkIn,
                                  @Param("checkOut") LocalDate checkOut,
                                  @Param("roomTypeId") Long roomTypeId);
}
