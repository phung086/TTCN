package com.example.hotel.dto.response;

import java.math.BigDecimal;

public class DashboardResponse {
    private Long totalGuests;
    private Long activeReservations;
    private BigDecimal monthlyRevenue;
    private Long totalRooms;
    
    // Constructor, Getters, Setters
    public DashboardResponse(Long totalGuests, Long activeReservations, BigDecimal monthlyRevenue, Long totalRooms) {
        this.totalGuests = totalGuests;
        this.activeReservations = activeReservations;
        this.monthlyRevenue = monthlyRevenue;
        this.totalRooms = totalRooms;
    }

    public Long getTotalGuests() { return totalGuests; }
    public void setTotalGuests(Long totalGuests) { this.totalGuests = totalGuests; }

    public Long getActiveReservations() { return activeReservations; }
    public void setActiveReservations(Long activeReservations) { this.activeReservations = activeReservations; }

    public BigDecimal getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(BigDecimal monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }

    public Long getTotalRooms() { return totalRooms; }
    public void setTotalRooms(Long totalRooms) { this.totalRooms = totalRooms; }
}
