package com.example.hotel.repository;

import com.example.hotel.domain.entity.HotelService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelServiceRepository extends JpaRepository<HotelService, Long> {
}
