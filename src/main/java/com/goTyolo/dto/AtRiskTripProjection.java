package com.goTyolo.dto;

import java.util.UUID;
import java.math.BigDecimal;

public interface AtRiskTripProjection {
    UUID getTripId();
    String getTitle();
    java.time.LocalDateTime getDepartureDate();
    BigDecimal getOccupancyPercent();
    String getReason();
}

