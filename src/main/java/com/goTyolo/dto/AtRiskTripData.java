package com.goTyolo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder
public class AtRiskTripData {
    private UUID tripId;
    private String title;
    private LocalDateTime departureDate;
    private BigDecimal occupancyPercent;
    private String reason;
}
