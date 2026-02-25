package com.goTyolo.models;

import com.goTyolo.enums.BookingState;
import java.util.UUID;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Table(name = "bookings")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID tripId;

    @Column(nullable = false)
    private UUID userId;

    private int numSeats;

    @Enumerated(EnumType.STRING)
    private BookingState state;

    @Column(precision = 10, scale = 2)
    private BigDecimal priceAtBooking;

    private String paymentReference;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime cancelledAt;

    @Column(precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(unique = true)
    private String idempotencyKey;

    private LocalDateTime updatedAt;
}
