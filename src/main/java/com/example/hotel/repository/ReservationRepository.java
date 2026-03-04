package com.example.hotel.repository;

import com.example.hotel.domain.entity.Reservation;
import com.example.hotel.domain.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    long countByStatus(ReservationStatus status);

    @Query("SELECT COALESCE(SUM(r.totalAmount), 0) FROM Reservation r WHERE r.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") ReservationStatus status);
}
