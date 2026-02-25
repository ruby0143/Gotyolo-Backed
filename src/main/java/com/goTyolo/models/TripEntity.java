package com.goTyolo.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import com.goTyolo.enums.TripStatus;
import com.goTyolo.dto.RefundPolicy;
import lombok.Data;

@Entity
@Data
@Table(name = "trips")
public class TripEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private String title;
    private String destination;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private Integer maxCapacity;
    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    private TripStatus status;

    @Embedded
    private RefundPolicy refundPolicy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Version
    private Long version;

}
