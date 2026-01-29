package com.example.hotel.controller;

import com.example.hotel.dto.request.ReservationCreateRequest;
import com.example.hotel.dto.request.ServiceRequestCreateRequest;
import com.example.hotel.dto.response.ReservationResponse;
import com.example.hotel.dto.response.RoomAvailabilityResponse;
import com.example.hotel.dto.response.ServiceRequestResponse;
import com.example.hotel.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/rooms/available")
    @Operation(summary = "Find available rooms")
    public List<RoomAvailabilityResponse> available(@RequestParam("checkIn") LocalDate checkIn,
                                                    @RequestParam("checkOut") LocalDate checkOut,
                                                    @RequestParam(value = "guests", required = false) Integer guests,
                                                    @RequestParam(value = "roomTypeId", required = false) Long roomTypeId) {
        return reservationService.findAvailableRooms(checkIn, checkOut, guests, roomTypeId);
    }

    @PostMapping("/reservations")
    @Operation(summary = "Create reservation")
    public ResponseEntity<ReservationResponse> create(@RequestBody @Validated ReservationCreateRequest request) {
        return new ResponseEntity<>(reservationService.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/reservations/{id}")
    @Operation(summary = "Get reservation")
    public ReservationResponse get(@PathVariable("id") Long id) {
        return reservationService.get(id);
    }

    @PatchMapping("/reservations/{id}/check-in")
    @Operation(summary = "Check-in reservation")
    public ReservationResponse checkIn(@PathVariable("id") Long id) {
        return reservationService.checkIn(id);
    }

    @PatchMapping("/reservations/{id}/check-out")
    @Operation(summary = "Check-out reservation")
    public ReservationResponse checkOut(@PathVariable("id") Long id) {
        return reservationService.checkOut(id);
    }

    @PatchMapping("/reservations/{id}/cancel")
    @Operation(summary = "Cancel reservation")
    public ReservationResponse cancel(@PathVariable("id") Long id) {
        return reservationService.cancel(id);
    }

    @PostMapping("/reservations/{id}/services")
    @Operation(summary = "Add service to reservation")
    public ResponseEntity<ServiceRequestResponse> addService(@PathVariable("id") Long id,
                                                             @RequestBody @Validated ServiceRequestCreateRequest request) {
        return new ResponseEntity<>(reservationService.addService(id, request), HttpStatus.CREATED);
    }
}
