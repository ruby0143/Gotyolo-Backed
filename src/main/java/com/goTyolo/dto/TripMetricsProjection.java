package com.goTyolo.dto;

import java.math.BigDecimal;
import java.util.UUID;

public interface TripMetricsProjection {
    UUID getTripId();
    String getTitle();
    int getTotalSeats();
    int getAvailableSeats();
    int getBookedSeats();
    int getConfirmed();
    int getPendingPayment();
    int getCancelled();
    int getExpired();
    BigDecimal getGrossRevenue();
    BigDecimal getRefundsIssued();
}
