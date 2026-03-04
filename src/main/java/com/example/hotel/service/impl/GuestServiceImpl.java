package com.example.hotel.service.impl;

import com.example.hotel.domain.entity.Guest;
import com.example.hotel.dto.request.GuestRequest;
import com.example.hotel.dto.response.GuestResponse;
import com.example.hotel.exception.NotFoundException;
import com.example.hotel.repository.GuestRepository;
import com.example.hotel.service.GuestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepository;

    public GuestServiceImpl(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @Override
    public GuestResponse create(GuestRequest request) {
        // Check if guest exists by email
        var existing = guestRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            return toResponse(existing.get());
        }

        Guest guest = new Guest();
        apply(request, guest);
        return toResponse(guestRepository.save(guest));
    }

    @Override
    public GuestResponse update(Long id, GuestRequest request) {
        Guest guest = guestRepository.findById(id).orElseThrow(() -> new NotFoundException("Guest not found"));
        apply(request, guest);
        return toResponse(guestRepository.save(guest));
    }

    @Override
    public void delete(Long id) {
        if (!guestRepository.existsById(id)) {
            throw new NotFoundException("Guest not found");
        }
        guestRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public GuestResponse get(Long id) {
        return guestRepository.findById(id).map(this::toResponse).orElseThrow(() -> new NotFoundException("Guest not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuestResponse> list() {
        return guestRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private void apply(GuestRequest request, Guest guest) {
        guest.setFirstName(request.getFirstName());
        guest.setLastName(request.getLastName());
        guest.setEmail(request.getEmail());
        guest.setPhone(request.getPhone());
    }

    private GuestResponse toResponse(Guest guest) {
        GuestResponse response = new GuestResponse();
        response.setId(guest.getId());
        response.setFirstName(guest.getFirstName());
        response.setLastName(guest.getLastName());
        response.setEmail(guest.getEmail());
        response.setPhone(guest.getPhone());
        response.setLoyaltyPoints(guest.getLoyaltyPoints());
        return response;
    }
}
