package com.goTyolo.dto;

import com.goTyolo.enums.BookingState;
import lombok.*;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingData {
    private UUID id;

    @NotNull
    private UUID tripId;

    @NotNull
    private UUID userId;

    @NotNull
    private Integer numSeats;
    private BookingState state;

    private BigDecimal priceAtBooking;
    private String paymentReference;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime cancelledAt;
    private BigDecimal refundAmount;
    private String idempotencyKey;
    private LocalDateTime updatedAt;
}
