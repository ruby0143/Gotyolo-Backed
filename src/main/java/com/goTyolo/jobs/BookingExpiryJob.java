package com.goTyolo.jobs;

import com.goTyolo.models.BookingEntity;
import com.goTyolo.repository.BookingRepository;
import com.goTyolo.enums.BookingState;
import com.goTyolo.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingExpiryJob {

    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final BookingService bookingService;

    // Runs every 1 minute to check for pending bookings that are older than 15 minutes and expire them
    @Scheduled(fixedRate = 60000)
    public void expirePendingBookings() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<BookingEntity> expiredBookings = bookingRepository.findByStateAndExpiry(BookingState.PENDING_PAYMENT, currentTime);
        for (BookingEntity booking : expiredBookings) {
            booking.setState(BookingState.EXPIRED);
            booking.setUpdatedAt(LocalDateTime.now());
            bookingService.releaseSeats(booking);
            log.info("Booking {} expired due to pending payment.", booking.getId());
        }
        if (!expiredBookings.isEmpty()) {
            bookingRepository.saveAll(expiredBookings);
        }
    }
}

