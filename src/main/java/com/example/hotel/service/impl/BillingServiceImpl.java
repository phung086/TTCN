package com.example.hotel.service.impl;

import com.example.hotel.domain.entity.Invoice;
import com.example.hotel.domain.entity.Payment;
import com.example.hotel.domain.enums.InvoiceStatus;
import com.example.hotel.domain.enums.PaymentStatus;
import com.example.hotel.dto.request.PaymentRequest;
import com.example.hotel.dto.response.InvoiceResponse;
import com.example.hotel.dto.response.PaymentResponse;
import com.example.hotel.exception.NotFoundException;
import com.example.hotel.repository.InvoiceRepository;
import com.example.hotel.repository.PaymentRepository;
import com.example.hotel.repository.ReservationRepository;
import com.example.hotel.service.BillingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Transactional
public class BillingServiceImpl implements BillingService {

    private final InvoiceRepository invoiceRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public BillingServiceImpl(InvoiceRepository invoiceRepository,
                              ReservationRepository reservationRepository,
                              PaymentRepository paymentRepository) {
        this.invoiceRepository = invoiceRepository;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceByReservation(Long reservationId) {
        Invoice invoice = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getReservation() != null && reservationId.equals(inv.getReservation().getId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Invoice not found"));
        return toResponse(invoice);
    }

    @Override
    public PaymentResponse addPayment(Long invoiceId, PaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException("Invoice not found"));
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());
        payment.setTransactionRef(request.getTransactionRef());
        payment.setPaidAt(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PAID);
        Payment saved = paymentRepository.save(payment);

        invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        invoiceRepository.save(invoice);
        return toResponse(saved);
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        InvoiceResponse resp = new InvoiceResponse();
        resp.setId(invoice.getId());
        resp.setReservationId(invoice.getReservation() != null ? invoice.getReservation().getId() : null);
        resp.setRoomSubtotal(invoice.getRoomSubtotal());
        resp.setServiceSubtotal(invoice.getServiceSubtotal());
        resp.setTax(invoice.getTax());
        resp.setDiscount(invoice.getDiscount());
        resp.setTotal(invoice.getTotal());
        resp.setStatus(invoice.getStatus());
        resp.setIssuedAt(invoice.getIssuedAt());
        resp.setPayments(invoice.getPayments().stream().map(this::toResponse).collect(Collectors.toList()));
        return resp;
    }

    private PaymentResponse toResponse(Payment payment) {
        PaymentResponse resp = new PaymentResponse();
        resp.setId(payment.getId());
        resp.setAmount(payment.getAmount());
        resp.setMethod(payment.getMethod());
        resp.setStatus(payment.getStatus());
        resp.setTransactionRef(payment.getTransactionRef());
        resp.setPaidAt(payment.getPaidAt());
        return resp;
    }
}
