package com.example.hotel.service.impl;

import com.example.hotel.domain.entity.Room;
import com.example.hotel.domain.entity.RoomType;
import com.example.hotel.domain.enums.HousekeepingStatus;
import com.example.hotel.domain.enums.RoomStatus;
import com.example.hotel.dto.request.RoomRequest;
import com.example.hotel.dto.response.RoomDetailResponse;
import com.example.hotel.exception.NotFoundException;
import com.example.hotel.repository.RoomRepository;
import com.example.hotel.repository.RoomTypeRepository;
import com.example.hotel.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    public RoomServiceImpl(RoomRepository roomRepository, RoomTypeRepository roomTypeRepository) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDetailResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoomDetailResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phòng với ID: " + id));
        return toResponse(room);
    }

    @Override
    public RoomDetailResponse createRoom(RoomRequest request) {
        Room room = new Room();
        updateRoomFromRequest(room, request);
        return toResponse(roomRepository.save(room));
    }

    @Override
    public RoomDetailResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phòng với ID: " + id));
        updateRoomFromRequest(room, request);
        return toResponse(roomRepository.save(room));
    }

    @Override
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new NotFoundException("Không tìm thấy phòng với ID: " + id);
        }
        roomRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllRoomTypes() {
        return roomTypeRepository.findAll().stream().map(rt -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", rt.getId());
            m.put("name", rt.getName());
            m.put("basePrice", rt.getBasePrice());
            m.put("capacity", rt.getCapacity());
            return m;
        }).collect(Collectors.toList());
    }

    private void updateRoomFromRequest(Room room, RoomRequest request) {
        room.setRoomNumber(request.getRoomNumber());
        room.setFloor(request.getFloor());
        if (request.getStatus() != null) {
            room.setStatus(request.getStatus());
        }
        if (request.getHousekeepingStatus() != null) {
            room.setHousekeepingStatus(request.getHousekeepingStatus());
        }

        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy loại phòng với ID: " + request.getRoomTypeId()));
        room.setRoomType(roomType);
    }

    private RoomDetailResponse toResponse(Room room) {
        RoomDetailResponse response = new RoomDetailResponse();
        response.setId(room.getId());
        response.setRoomNumber(room.getRoomNumber());
        response.setFloor(room.getFloor());
        response.setStatus(room.getStatus());
        response.setHousekeepingStatus(room.getHousekeepingStatus());
        
        if (room.getRoomType() != null) {
            response.setRoomTypeId(room.getRoomType().getId());
            response.setRoomTypeName(room.getRoomType().getName());
            response.setBasePrice(room.getRoomType().getBasePrice());
            response.setCapacity(room.getRoomType().getCapacity());
        }
        
        return response;
    }
}
