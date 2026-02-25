package com.goTyolo.controllers;

import com.goTyolo.dto.AtRiskTripData;
import com.goTyolo.dto.TripData;
import com.goTyolo.dto.TripMetricsData;
import com.goTyolo.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private final TripService tripService;

    @GetMapping
    public List<TripData> getTrips() {
        return tripService.getAllTrips();
    }

    @GetMapping("/{tripId}")
    public TripData getTripById(@PathVariable UUID tripId) {
        return tripService.getTripById(tripId);
    }

    @PostMapping("/create")
    public TripData createTrip(@RequestBody TripData tripData) {
        return tripService.createTrip(tripData);
    }

    @GetMapping("/{tripId}/metrics")
    public TripMetricsData getTripMetrics(@PathVariable UUID tripId) {
        return tripService.getTripMetrics(tripId);
    }

    @GetMapping("/at-risk")
    public List<AtRiskTripData> getAtRiskTrips() {
        return tripService.getAtRiskTripData();
    }
}
