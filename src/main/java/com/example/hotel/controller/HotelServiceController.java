package com.example.hotel.controller;

import com.example.hotel.domain.entity.HotelService;
import com.example.hotel.repository.HotelServiceRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/services")
@Tag(name = "Hotel Services", description = "APIs for hotel services catalog")
public class HotelServiceController {

    private final HotelServiceRepository hotelServiceRepository;

    public HotelServiceController(HotelServiceRepository hotelServiceRepository) {
        this.hotelServiceRepository = hotelServiceRepository;
    }

    @GetMapping
    @Operation(summary = "Get all available hotel services")
    public List<Map<String, Object>> getAllServices() {
        return hotelServiceRepository.findAll().stream()
                .map(s -> Map.<String, Object>of(
                        "id", s.getId(),
                        "name", s.getName(),
                        "category", s.getCategory(),
                        "unitPrice", s.getUnitPrice()
                ))
                .collect(Collectors.toList());
    }
}
