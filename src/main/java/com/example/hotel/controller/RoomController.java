package com.example.hotel.controller;

import com.example.hotel.dto.request.RoomRequest;
import com.example.hotel.dto.response.RoomDetailResponse;
import com.example.hotel.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rooms")
@Tag(name = "Room Management", description = "Admin APIs for managing rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    @Operation(summary = "Get all rooms")
    public List<RoomDetailResponse> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/types")
    @Operation(summary = "Get all room types")
    public List<Map<String, Object>> getRoomTypes() {
        return roomService.getAllRoomTypes();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID")
    public RoomDetailResponse getRoomById(@PathVariable("id") Long id) {
        return roomService.getRoomById(id);
    }

    @PostMapping
    @Operation(summary = "Create new room")
    public ResponseEntity<RoomDetailResponse> createRoom(@RequestBody @Validated RoomRequest request) {
        return new ResponseEntity<>(roomService.createRoom(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room")
    public RoomDetailResponse updateRoom(@PathVariable("id") Long id, @RequestBody @Validated RoomRequest request) {
        return roomService.updateRoom(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete room")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable("id") Long id) {
        roomService.deleteRoom(id);
    }
}
