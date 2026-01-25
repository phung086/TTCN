package com.example.hotel.controller;

import com.example.hotel.dto.request.PaymentRequest;
import com.example.hotel.dto.response.InvoiceResponse;
import com.example.hotel.dto.response.PaymentResponse;
import com.example.hotel.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/reservations/{id}/invoice")
    @Operation(summary = "Get invoice for reservation")
    public InvoiceResponse getInvoice(@PathVariable Long id) {
        return billingService.getInvoiceByReservation(id);
    }

    @PostMapping("/invoices/{id}/payments")
    @Operation(summary = "Add payment to invoice")
    public ResponseEntity<PaymentResponse> addPayment(@PathVariable Long id,
                                                      @RequestBody @Validated PaymentRequest request) {
        return new ResponseEntity<>(billingService.addPayment(id, request), HttpStatus.CREATED);
    }
}
