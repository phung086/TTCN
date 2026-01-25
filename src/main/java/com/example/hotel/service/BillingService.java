package com.example.hotel.service;

import com.example.hotel.dto.request.PaymentRequest;
import com.example.hotel.dto.response.InvoiceResponse;
import com.example.hotel.dto.response.PaymentResponse;

public interface BillingService {
    InvoiceResponse getInvoiceByReservation(Long reservationId);
    PaymentResponse addPayment(Long invoiceId, PaymentRequest request);
}
