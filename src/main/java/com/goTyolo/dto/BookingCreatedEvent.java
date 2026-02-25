package com.goTyolo.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class BookingCreatedEvent {

    private final UUID bookingId;
}
