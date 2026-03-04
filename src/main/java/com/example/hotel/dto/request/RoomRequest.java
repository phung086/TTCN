package com.example.hotel.dto.request;

import com.example.hotel.domain.enums.HousekeepingStatus;
import com.example.hotel.domain.enums.RoomStatus;
import jakarta.validation.constraints.NotNull;

public class RoomRequest {
    @NotNull(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "Floor is required")
    private Integer floor;

    private RoomStatus status;
    
    private HousekeepingStatus housekeepingStatus;

    @NotNull(message = "Room type ID is required")
    private Long roomTypeId;

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }
    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }
    public HousekeepingStatus getHousekeepingStatus() { return housekeepingStatus; }
    public void setHousekeepingStatus(HousekeepingStatus housekeepingStatus) { this.housekeepingStatus = housekeepingStatus; }
    public Long getRoomTypeId() { return roomTypeId; }
    public void setRoomTypeId(Long roomTypeId) { this.roomTypeId = roomTypeId; }
}
