package com.example.hotel.service;

import com.example.hotel.dto.request.GuestRequest;
import com.example.hotel.dto.response.GuestResponse;
import java.util.List;

public interface GuestService {
    GuestResponse create(GuestRequest request);
    GuestResponse update(Long id, GuestRequest request);
    void delete(Long id);
    GuestResponse get(Long id);
    List<GuestResponse> list();
}
