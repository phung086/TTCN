package com.example.hotel.controller;

import com.example.hotel.dto.request.GuestRequest;
import com.example.hotel.dto.response.GuestResponse;
import com.example.hotel.service.GuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/guests")
@Tag(name = "Guests")
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @PostMapping
    @Operation(summary = "Create guest")
    public ResponseEntity<GuestResponse> create(@RequestBody @Validated GuestRequest request) {
        return new ResponseEntity<>(guestService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List guests")
    public List<GuestResponse> list() {
        return guestService.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get guest by id")
    public GuestResponse get(@PathVariable("id") Long id) {
        return guestService.get(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update guest")
    public GuestResponse update(@PathVariable("id") Long id, @RequestBody @Validated GuestRequest request) {
        return guestService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete guest")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        guestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
