package com.goTyolo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class TripMetricsData {
    private UUID tripId;
    private String title;
    private int occupancyPercent;
    private int totalSeats;
    private int bookedSeats;
    private int availableSeats;
    private BookingSummary bookingSummary;
    private FinancialSummary financial;

    @Data
    @Builder
    @AllArgsConstructor
    public static class BookingSummary {
        // Getters and setters
        private int confirmed;
        private int pendingPayment;
        private int cancelled;
        private int expired;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class FinancialSummary {
        private BigDecimal grossRevenue;
        private BigDecimal refundsIssued;
        private BigDecimal netRevenue;

    }
}
