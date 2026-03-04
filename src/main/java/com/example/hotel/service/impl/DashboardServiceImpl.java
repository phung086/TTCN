package com.example.hotel.service.impl;

import com.example.hotel.domain.enums.ReservationStatus;
import com.example.hotel.dto.response.DashboardResponse;
import com.example.hotel.repository.GuestRepository;
import com.example.hotel.repository.ReservationRepository;
import com.example.hotel.repository.RoomRepository;
import com.example.hotel.service.DashboardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final GuestRepository guestRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public DashboardServiceImpl(GuestRepository guestRepository, 
                                ReservationRepository reservationRepository,
                                RoomRepository roomRepository) {
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public DashboardResponse getSummary() {
        // 1. Total Guests
        long totalGuests = guestRepository.count();

        // 2. Active Reservations (CONFIRMED + CHECKED_IN)
        long confirmed = reservationRepository.countByStatus(ReservationStatus.CONFIRMED);
        long checkedIn = reservationRepository.countByStatus(ReservationStatus.CHECKED_IN);
        long activeReservations = confirmed + checkedIn;

        // 3. Total Revenue (from CHECKED_OUT reservations)
        BigDecimal revenue = reservationRepository.sumTotalAmountByStatus(ReservationStatus.CHECKED_OUT);

        // 4. Total Rooms
        long totalRooms = roomRepository.count();

        return new DashboardResponse(totalGuests, activeReservations, revenue, totalRooms);
    }
}
