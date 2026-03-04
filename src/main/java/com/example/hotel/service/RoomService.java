package com.example.hotel.service;

import com.example.hotel.dto.request.RoomRequest;
import com.example.hotel.dto.response.RoomDetailResponse;
import java.util.List;
import java.util.Map;

public interface RoomService {
    List<RoomDetailResponse> getAllRooms();
    RoomDetailResponse getRoomById(Long id);
    RoomDetailResponse createRoom(RoomRequest request);
    RoomDetailResponse updateRoom(Long id, RoomRequest request);
    void deleteRoom(Long id);
    List<Map<String, Object>> getAllRoomTypes();
}
