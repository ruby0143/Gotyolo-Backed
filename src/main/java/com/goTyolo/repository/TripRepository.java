package com.goTyolo.repository;

import com.goTyolo.dto.AtRiskTripProjection;
import com.goTyolo.dto.TripMetricsData;
import com.goTyolo.dto.TripMetricsProjection;
import com.goTyolo.models.TripEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<TripEntity, UUID> {


    @Query(nativeQuery = true, value = """
            
                        SELECT
                t.id AS tripId,
                t.title AS title,
                t.max_capacity AS totalSeats,
                t.available_seats AS availableSeats,
            
                COALESCE(SUM(b.num_seats), 0) AS bookedSeats,
            
                COALESCE(SUM(CASE WHEN b.state = 'CONFIRMED' THEN b.num_seats ELSE 0 END), 0) AS confirmed,
                COALESCE(SUM(CASE WHEN b.state = 'PENDING_PAYMENT' THEN b.num_seats ELSE 0 END), 0) AS pendingPayment,
                COALESCE(SUM(CASE WHEN b.state = 'CANCELLED' THEN b.num_seats ELSE 0 END), 0) AS cancelled,
                COALESCE(SUM(CASE WHEN b.state = 'EXPIRED' THEN b.num_seats ELSE 0 END), 0) AS expired,
            
                COALESCE(SUM(
                    CASE WHEN b.state = 'CONFIRMED' or b.state = 'CANCELLED' THEN b.price_at_booking ELSE 0 END
                ), 0) AS grossRevenue,
            
                COALESCE(SUM(
                    CASE WHEN b.state = 'CANCELLED' THEN b.refund_amount ELSE 0 END
                ), 0) AS refundsIssued
            FROM trips t
            LEFT JOIN bookings b ON b.trip_id = t.id
            WHERE t.id = :tripId
            GROUP BY t.id, t.title, t.max_capacity, t.available_seats
            """)
    Optional<TripMetricsProjection> findTripMetricsById(UUID tripId);


    @Query(value = """
            SELECT
                t.id AS tripId,
                t.title AS title,
                t.start_date AS departureDate,
                ROUND(
                    ((t.max_capacity - t.available_seats) * 100.0)
                    / NULLIF(t.max_capacity, 0)
                ) AS occupancyPercent,
                'Low occupancy with imminent departure' AS reason
            FROM trips t
            WHERE t.start_date BETWEEN CURRENT_DATE
                  AND CURRENT_DATE + INTERVAL '7 days'
            AND (
                    ((t.max_capacity - t.available_seats) * 100.0)
                    / NULLIF(t.max_capacity, 0)
                ) < 50
            ORDER BY t.start_date
            """, nativeQuery = true)
    List<AtRiskTripProjection> findAtRiskTrips();

}
