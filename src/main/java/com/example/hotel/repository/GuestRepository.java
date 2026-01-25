package com.example.hotel.repository;

import com.example.hotel.domain.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    boolean existsByEmail(String email);
}
