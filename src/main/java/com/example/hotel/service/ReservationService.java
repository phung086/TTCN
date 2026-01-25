package com.example.hotel.service;

import com.example.hotel.dto.request.ReservationCreateRequest;
import com.example.hotel.dto.request.ServiceRequestCreateRequest;
import com.example.hotel.dto.response.ReservationResponse;
import com.example.hotel.dto.response.RoomAvailabilityResponse;
import com.example.hotel.dto.response.ServiceRequestResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {
    List<RoomAvailabilityResponse> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, Integer guests, Long roomTypeId);
    ReservationResponse create(ReservationCreateRequest request);
    ReservationResponse get(Long id);
    ReservationResponse checkIn(Long id);
    ReservationResponse checkOut(Long id);
    ReservationResponse cancel(Long id);
    ServiceRequestResponse addService(Long reservationId, ServiceRequestCreateRequest request);
}
