package com.goTyolo.service;


import com.goTyolo.dto.*;
import com.goTyolo.exception.TripNotFoundException;
import com.goTyolo.models.TripEntity;
import com.goTyolo.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripService {

    @Autowired
    private final TripRepository tripRepository;

    public List<TripData> getAllTrips(){
        return mapAllToResponse(tripRepository.findAll());
    }

    public TripData getTripById(UUID tripId){
        TripEntity tripEntity = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + tripId));
        return mapToResponse(tripEntity);
    }

    public TripData createTrip(TripData tripData){
        TripEntity tripEntity = new TripEntity();
        tripEntity.setDestination(tripData.getDestination());
        tripEntity.setStartDate(tripData.getStartDate());
        tripEntity.setEndDate(tripData.getEndDate());
        tripEntity.setAvailableSeats(tripData.getAvailableSeats());
        tripEntity.setMaxCapacity(tripData.getMaxCapacity());
        tripEntity.setPrice(tripData.getPrice());
        tripEntity.setTitle(tripData.getTitle());
        tripEntity.setStatus(tripData.getStatus());
        tripEntity.setRefundPolicy(tripData.getRefundPolicy());
        tripEntity.setCreatedAt(LocalDateTime.now());
        tripEntity.setUpdatedAt(LocalDateTime.now());
        TripEntity savedEntity = tripRepository.save(tripEntity);
        return mapToResponse(savedEntity);
    }

    public TripMetricsData getTripMetrics(UUID tripId){
        TripMetricsProjection metricsProjection = tripRepository.findTripMetricsById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + tripId));

        return mapToMetricsResponse(metricsProjection);
    }

    public List<AtRiskTripData> getAtRiskTripData(){
        List<AtRiskTripProjection> riskTrips = tripRepository.findAtRiskTrips();
        return mapToAtRiskTripResponse(riskTrips);
    }

    private List<AtRiskTripData> mapToAtRiskTripResponse(List<AtRiskTripProjection> projections){
        return projections.stream()
                .map(projection -> AtRiskTripData.builder()
                        .tripId(projection.getTripId())
                        .title(projection.getTitle())
                        .departureDate(projection.getDepartureDate())
                        .occupancyPercent(projection.getOccupancyPercent())
                        .reason(projection.getReason())
                        .build())
                .toList();
    }

    private TripMetricsData mapToMetricsResponse(TripMetricsProjection projection) {
        return TripMetricsData.builder()
                .tripId(projection.getTripId())
                .title(projection.getTitle())
                .totalSeats(projection.getTotalSeats())
                .availableSeats(projection.getAvailableSeats())
                .bookedSeats(projection.getBookedSeats())
                .occupancyPercent((int) ((double) projection.getBookedSeats() / projection.getTotalSeats() * 100))
                .bookingSummary(new TripMetricsData.BookingSummary(
                        projection.getConfirmed(),
                        projection.getPendingPayment(),
                        projection.getCancelled(),
                        projection.getExpired()
                ))
                .financial(new TripMetricsData.FinancialSummary(
                        projection.getGrossRevenue(),
                        projection.getRefundsIssued(),
                        projection.getGrossRevenue().subtract(projection.getRefundsIssued())
                ))
                .build();
    }

    private List<TripData> mapAllToResponse(List<TripEntity> tripEntities){
        return tripEntities.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private TripData mapToResponse(TripEntity tripEntity){
        return TripData.builder()
                .id(tripEntity.getId())
                .title(tripEntity.getTitle())
                .destination(tripEntity.getDestination())
                .startDate(tripEntity.getStartDate())
                .endDate(tripEntity.getEndDate())
                .price(tripEntity.getPrice())
                .maxCapacity(tripEntity.getMaxCapacity())
                .availableSeats(tripEntity.getAvailableSeats())
                .status(tripEntity.getStatus())
                .refundPolicy(tripEntity.getRefundPolicy())
                .createdAt(tripEntity.getCreatedAt())
                .updatedAt(tripEntity.getUpdatedAt())
                .build();
    }
}
