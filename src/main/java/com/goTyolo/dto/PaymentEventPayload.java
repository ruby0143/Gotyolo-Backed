package com.goTyolo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentEventPayload {
    private UUID bookingId;
    private String state;
    private String idempotencyKey;
}
