package com.example.hotel.repository;

import com.example.hotel.domain.entity.Room;
import com.example.hotel.domain.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByStatus(RoomStatus status);
}
