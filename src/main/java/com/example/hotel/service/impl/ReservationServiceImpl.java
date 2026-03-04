package com.example.hotel.service.impl;

import com.example.hotel.domain.entity.Invoice;
import com.example.hotel.domain.entity.Reservation;
import com.example.hotel.domain.entity.ReservationRoom;
import com.example.hotel.domain.entity.Room;
import com.example.hotel.domain.entity.ServiceRequest;
import com.example.hotel.domain.enums.HousekeepingStatus;
import com.example.hotel.domain.enums.InvoiceStatus;
import com.example.hotel.domain.enums.ReservationStatus;
import com.example.hotel.domain.enums.RoomStatus;
import com.example.hotel.dto.request.ReservationCreateRequest;
import com.example.hotel.dto.request.ServiceRequestCreateRequest;
import com.example.hotel.dto.response.GuestResponse;
import com.example.hotel.dto.response.ReservationResponse;
import com.example.hotel.dto.response.RoomAvailabilityResponse;
import com.example.hotel.dto.response.ServiceRequestResponse;
import com.example.hotel.exception.BusinessException;
import com.example.hotel.exception.NotFoundException;
import com.example.hotel.repository.GuestRepository;
import com.example.hotel.repository.HotelServiceRepository;
import com.example.hotel.repository.InvoiceRepository;
import com.example.hotel.repository.ReservationRepository;
import com.example.hotel.repository.RoomRepository;
import com.example.hotel.repository.ServiceRequestRepository;
import com.example.hotel.service.ReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private final InvoiceRepository invoiceRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  GuestRepository guestRepository,
                                  RoomRepository roomRepository,
                                  HotelServiceRepository hotelServiceRepository,
                                  ServiceRequestRepository serviceRequestRepository,
                                  InvoiceRepository invoiceRepository) {
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
        this.hotelServiceRepository = hotelServiceRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomAvailabilityResponse> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, Integer guests, Long roomTypeId) {
        return roomRepository.findAvailableRooms(checkIn, checkOut, roomTypeId).stream()
                .map(r -> {
                    RoomAvailabilityResponse resp = new RoomAvailabilityResponse();
                    resp.setRoomId(r.getId());
                    resp.setRoomNumber(r.getRoomNumber());
                    if (r.getRoomType() != null) {
                        resp.setRoomType(r.getRoomType().getName());
                        resp.setCapacity(r.getRoomType().getCapacity());
                        resp.setPrice(r.getRoomType().getBasePrice());
                    }
                    resp.setCurrentStatus(r.getStatus() != null ? r.getStatus().name() : "AVAILABLE");
                    return resp;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ReservationResponse create(ReservationCreateRequest request) {
        if (request.getCheckInDate().isAfter(request.getCheckOutDate())) {
            throw new BusinessException("Ngày nhận phòng phải trước ngày trả phòng");
        }
        
        // Validate room availability
        List<Room> availableRooms = roomRepository.findAvailableRooms(request.getCheckInDate(), request.getCheckOutDate(), null);
        List<Long> availableRoomIds = availableRooms.stream().map(Room::getId).collect(Collectors.toList());
        
        List<Room> rooms = roomRepository.findAllById(request.getRoomIds());
        if (rooms.isEmpty()) {
            throw new BusinessException("Không tìm thấy phòng nào");
        }
        
        for (Room room : rooms) {
            if (!availableRoomIds.contains(room.getId())) {
                throw new BusinessException("Phòng " + room.getRoomNumber() + " không có sẵn trong khoảng thời gian đã chọn");
            }
        }

        Reservation reservation = new Reservation();
        reservation.setGuest(guestRepository.findById(request.getGuestId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy khách hàng")));
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setStatus(ReservationStatus.PENDING);

        // Reuse existing rooms list from availability check
        BigDecimal totalAmount = BigDecimal.ZERO;
        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (nights < 1) nights = 1;

        for (Room room : rooms) {
            BigDecimal price = room.getRoomType() != null ? room.getRoomType().getBasePrice() : BigDecimal.ZERO;
            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(nights)));

            ReservationRoom rr = new ReservationRoom();
            rr.setReservation(reservation);
            rr.setRoom(room);
            rr.setRatePerNight(price);
            rr.setGuests(1);
            reservation.getReservationRooms().add(rr);
        }
        reservation.setTotalAmount(totalAmount);
        Reservation saved = reservationRepository.save(reservation);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getAll() {
        return reservationRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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
        if (reservation.getStatus() == ReservationStatus.CHECKED_IN) {
            throw new BusinessException("Đặt phòng đã được check-in rồi");
        }
        if (reservation.getStatus() == ReservationStatus.CHECKED_OUT || reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new BusinessException("Không thể check-in đặt phòng có trạng thái: " + reservation.getStatus());
        }
        reservation.setStatus(ReservationStatus.CHECKED_IN);
        // Update all rooms to OCCUPIED
        for (ReservationRoom rr : reservation.getReservationRooms()) {
            if (rr.getRoom() != null) {
                rr.getRoom().setStatus(RoomStatus.OCCUPIED);
                roomRepository.save(rr.getRoom());
            }
        }
        return toResponse(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponse checkOut(Long id) {
        Reservation reservation = findReservation(id);
        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new BusinessException("Chỉ có thể check-out đặt phòng đang ở trạng thái: CHECKED_IN");
        }
        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        // Free up rooms
        for (ReservationRoom rr : reservation.getReservationRooms()) {
            if (rr.getRoom() != null) {
                rr.getRoom().setStatus(RoomStatus.AVAILABLE);
                rr.getRoom().setHousekeepingStatus(HousekeepingStatus.NEEDS_CLEANING);
                roomRepository.save(rr.getRoom());
            }
        }
        // Auto-generate invoice if not already exists
        invoiceRepository.findByReservationId(reservation.getId()).orElseGet(() -> {
            BigDecimal roomSubtotal = reservation.getReservationRooms().stream()
                    .map(rr -> {
                        long nights = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
                        if (nights < 1) nights = 1;
                        return rr.getRatePerNight().multiply(BigDecimal.valueOf(nights));
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal serviceSubtotal = reservation.getServiceRequests().stream()
                    .map(ServiceRequest::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal tax = roomSubtotal.add(serviceSubtotal).multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal total = roomSubtotal.add(serviceSubtotal).add(tax);
            Invoice invoice = new Invoice();
            invoice.setReservation(reservation);
            invoice.setRoomSubtotal(roomSubtotal);
            invoice.setServiceSubtotal(serviceSubtotal);
            invoice.setTax(tax);
            invoice.setDiscount(BigDecimal.ZERO);
            invoice.setTotal(total);
            invoice.setStatus(InvoiceStatus.ISSUED);
            invoice.setIssuedAt(LocalDateTime.now());
            // Update reservation total to match invoice
            reservation.setTotalAmount(total);
            return invoiceRepository.save(invoice);
        });
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
        var service = hotelServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new NotFoundException("Service not found"));
        sr.setService(service);
        sr.setQuantity(request.getQuantity());
        BigDecimal serviceTotal = service.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        sr.setAmount(serviceTotal);
        
        // Update reservation total amount
        reservation.setTotalAmount(reservation.getTotalAmount().add(serviceTotal));
        reservationRepository.save(reservation);

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
        if (reservation.getGuest() != null) {
            resp.setGuestId(reservation.getGuest().getId());
            resp.setGuestName(reservation.getGuest().getLastName() + " " + reservation.getGuest().getFirstName());
            GuestResponse guestResp = new GuestResponse();
            guestResp.setId(reservation.getGuest().getId());
            guestResp.setFirstName(reservation.getGuest().getFirstName());
            guestResp.setLastName(reservation.getGuest().getLastName());
            guestResp.setEmail(reservation.getGuest().getEmail());
            guestResp.setPhone(reservation.getGuest().getPhone());
            guestResp.setLoyaltyPoints(reservation.getGuest().getLoyaltyPoints());
            resp.setGuest(guestResp);
        }
        resp.setTotalAmount(reservation.getTotalAmount());
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
