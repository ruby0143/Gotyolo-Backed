package com.goTyolo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "payment_events")
public class PaymentEventEntity {
    @Id
    private String idempotencyKey;

    private String state;
    private UUID bookingId;
}

