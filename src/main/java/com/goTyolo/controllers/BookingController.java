package com.goTyolo.controllers;

import com.goTyolo.dto.BookingData;
import com.goTyolo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private final BookingService bookingService;

    @PostMapping("/create")
    public BookingData createBooking(@RequestBody BookingData bookingData) {
        return bookingService.createBooking(bookingData);
    }

    @GetMapping("/{bookingId}")
    public BookingData getBookingById(@PathVariable UUID bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    @PostMapping("/{bookingId}/cancel")
    public BookingData cancelBooking(@PathVariable UUID bookingId) {
        return bookingService.cancelBooking(bookingId);
    }
}
