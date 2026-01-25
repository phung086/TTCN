package com.example.hotel.repository;

import com.example.hotel.domain.entity.ReservationRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRoomRepository extends JpaRepository<ReservationRoom, Long> {
}
