package com.example.hotel.service.impl;

import com.example.hotel.domain.entity.Reservation;
import com.example.hotel.domain.entity.ReservationRoom;
import com.example.hotel.domain.entity.Room;
import com.example.hotel.domain.entity.ServiceRequest;
import com.example.hotel.domain.enums.ReservationStatus;
import com.example.hotel.domain.enums.RoomStatus;
import com.example.hotel.dto.request.ReservationCreateRequest;
import com.example.hotel.dto.request.ServiceRequestCreateRequest;
import com.example.hotel.dto.response.ReservationResponse;
import com.example.hotel.dto.response.RoomAvailabilityResponse;
import com.example.hotel.dto.response.ServiceRequestResponse;
import com.example.hotel.exception.BusinessException;
import com.example.hotel.exception.NotFoundException;
import com.example.hotel.repository.GuestRepository;
import com.example.hotel.repository.HotelServiceRepository;
import com.example.hotel.repository.ReservationRepository;
import com.example.hotel.repository.RoomRepository;
import com.example.hotel.repository.ServiceRequestRepository;
import com.example.hotel.service.ReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final HotelServiceRepository hotelServiceRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  GuestRepository guestRepository,
                                  RoomRepository roomRepository,
                                  HotelServiceRepository hotelServiceRepository,
                                  ServiceRequestRepository serviceRequestRepository) {
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
        this.hotelServiceRepository = hotelServiceRepository;
        this.serviceRequestRepository = serviceRequestRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomAvailabilityResponse> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, Integer guests, Long roomTypeId) {
        return roomRepository.findByStatus(RoomStatus.AVAILABLE).stream()
                .filter(r -> roomTypeId == null || (r.getRoomType() != null && roomTypeId.equals(r.getRoomType().getId())))
                .map(r -> {
                    RoomAvailabilityResponse resp = new RoomAvailabilityResponse();
                    resp.setRoomId(r.getId());
                    resp.setRoomNumber(r.getRoomNumber());
                    resp.setRoomType(r.getRoomType() != null ? r.getRoomType().getName() : null);
                    resp.setCapacity(r.getRoomType() != null ? r.getRoomType().getCapacity() : null);
                    return resp;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ReservationResponse create(ReservationCreateRequest request) {
        if (request.getCheckInDate().isAfter(request.getCheckOutDate())) {
            throw new BusinessException("Check-in date must be before check-out date");
        }
        Reservation reservation = new Reservation();
        reservation.setGuest(guestRepository.findById(request.getGuestId())
                .orElseThrow(() -> new NotFoundException("Guest not found")));
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setStatus(ReservationStatus.PENDING);

        List<Room> rooms = roomRepository.findAllById(request.getRoomIds());
        if (rooms.isEmpty()) {
            throw new BusinessException("No rooms found");
        }
        for (Room room : rooms) {
            ReservationRoom rr = new ReservationRoom();
            rr.setReservation(reservation);
            rr.setRoom(room);
            rr.setRatePerNight(room.getRoomType() != null ? room.getRoomType().getBasePrice() : BigDecimal.ZERO);
            rr.setGuests(1);
            reservation.getReservationRooms().add(rr);
        }
        Reservation saved = reservationRepository.save(reservation);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse get(Long id) {
        return reservationRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));
    }

    @Override
    public ReservationResponse checkIn(Long id) {
        Reservation reservation = findReservation(id);
        reservation.setStatus(ReservationStatus.CHECKED_IN);
        return toResponse(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponse checkOut(Long id) {
        Reservation reservation = findReservation(id);
        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        return toResponse(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponse cancel(Long id) {
        Reservation reservation = findReservation(id);
        reservation.setStatus(ReservationStatus.CANCELED);
        return toResponse(reservationRepository.save(reservation));
    }

    @Override
    public ServiceRequestResponse addService(Long reservationId, ServiceRequestCreateRequest request) {
        Reservation reservation = findReservation(reservationId);
        ServiceRequest sr = new ServiceRequest();
        sr.setReservation(reservation);
        sr.setService(hotelServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new NotFoundException("Service not found")));
        sr.setQuantity(request.getQuantity());
        sr.setAmount(sr.getService().getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        ServiceRequest saved = serviceRequestRepository.save(sr);
        return toResponse(saved);
    }

    private Reservation findReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));
    }

    private ReservationResponse toResponse(Reservation reservation) {
        ReservationResponse resp = new ReservationResponse();
        resp.setId(reservation.getId());
        resp.setGuestId(reservation.getGuest() != null ? reservation.getGuest().getId() : null);
        resp.setCheckInDate(reservation.getCheckInDate());
        resp.setCheckOutDate(reservation.getCheckOutDate());
        resp.setStatus(reservation.getStatus());
        List<Long> roomIds = reservation.getReservationRooms().stream()
                .map(rr -> rr.getRoom() != null ? rr.getRoom().getId() : null)
                .collect(Collectors.toList());
        resp.setRoomIds(roomIds);
        return resp;
    }

    private ServiceRequestResponse toResponse(ServiceRequest sr) {
        ServiceRequestResponse resp = new ServiceRequestResponse();
        resp.setId(sr.getId());
        resp.setServiceId(sr.getService() != null ? sr.getService().getId() : null);
        resp.setServiceName(sr.getService() != null ? sr.getService().getName() : null);
        resp.setQuantity(sr.getQuantity());
        resp.setAmount(sr.getAmount());
        resp.setStatus(sr.getStatus());
        return resp;
    }
}
