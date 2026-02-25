package com.goTyolo.controllers;

import com.goTyolo.dto.BookingData;
import com.goTyolo.dto.PaymentEventPayload;
import com.goTyolo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    private final BookingService bookingService;

    @PostMapping("/webhook")
    public ResponseEntity<?> handlePaymentWebhook(@RequestBody String payload) {

        ObjectMapper objectMapper = new ObjectMapper();
            try {
                PaymentEventPayload paymentResponse = objectMapper.readValue(payload, PaymentEventPayload.class);
                BookingData bookingData = bookingService.validateAndUpdateBookingState(paymentResponse);
                return ResponseEntity.accepted().build();

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().build();
            }
    }

}
