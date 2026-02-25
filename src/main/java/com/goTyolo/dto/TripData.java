package com.goTyolo.dto;

import com.goTyolo.enums.TripStatus;
import lombok.*;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripData {
    private UUID id;

    @NotNull
    private String title;

    @NotNull
    private String destination;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Integer maxCapacity;

    @NotNull
    private Integer availableSeats;

    private TripStatus status;
    private RefundPolicy refundPolicy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
