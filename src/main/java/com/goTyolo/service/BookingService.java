package com.goTyolo.service;

import com.goTyolo.dto.BookingCreatedEvent;
import com.goTyolo.dto.BookingData;
import com.goTyolo.dto.PaymentEventPayload;
import com.goTyolo.enums.BookingState;
import com.goTyolo.enums.PaymentState;
import com.goTyolo.enums.TripStatus;
import com.goTyolo.exception.BookingCancellationException;
import com.goTyolo.models.BookingEntity;
import com.goTyolo.models.PaymentEventEntity;
import com.goTyolo.models.TripEntity;
import com.goTyolo.repository.BookingRepository;
import com.goTyolo.repository.PaymentEventRepository;
import com.goTyolo.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CancellationException;

import com.goTyolo.exception.TripNotFoundException;
import com.goTyolo.exception.BookingNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final TripRepository tripRepository;

    @Autowired
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    private final PaymentEventRepository paymentEventRepository;

    @Transactional
    public BookingData createBooking(BookingData bookingData) {
        TripEntity tripEntity = isBookingPossible(bookingData);
        updateTripSeats(tripEntity, bookingData.getNumSeats());
        BookingEntity bookingEntity = createPendingBooking(bookingData,tripEntity);
        publishBookingCreatedEvent(bookingEntity.getId());
        return mapToData(bookingEntity);
    }

    public BookingData getBookingById(UUID bookingId) {
        BookingEntity bookingEntity = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));
        return mapToData(bookingEntity);
    }

    @Transactional
    public BookingData validateAndUpdateBookingState(PaymentEventPayload eventPayload) {
        Optional<PaymentEventEntity> paymentEvent = paymentEventRepository.findById(eventPayload.getIdempotencyKey());

        if (paymentEvent.isPresent()){
            return null;
        }
        else{
            Optional<BookingEntity> bookingEntity = bookingRepository.findByIdForUpdate(eventPayload.getBookingId());
            if (bookingEntity.isPresent()){
                BookingEntity booking = bookingEntity.get();
                if (booking.getState() == BookingState.PENDING_PAYMENT) {
                    if(LocalDateTime.now().isAfter(booking.getExpiresAt())){
                        booking.setState(BookingState.EXPIRED);
                        releaseSeats(booking);
                    }
                    else{
                        if (eventPayload.getState().equals(PaymentState.SUCCESS.toString())) {
                            booking.setState(BookingState.CONFIRMED);
                        } else {
                            booking.setState(BookingState.EXPIRED);
                            releaseSeats(booking);
                        }

                        // Save the payment event for idempotency tracking
                        PaymentEventEntity paymentEventEntity = new PaymentEventEntity();
                        paymentEventEntity.setIdempotencyKey(eventPayload.getIdempotencyKey());
                        paymentEventEntity.setState(PaymentState.SUCCESS.toString());
                        paymentEventEntity.setBookingId(eventPayload.getBookingId());
                        paymentEventRepository.save(paymentEventEntity);

                    }
                    booking.setUpdatedAt(LocalDateTime.now());
                    bookingRepository.save(booking);
                }
                return mapToData(booking);
            }
            else {
                throw new BookingNotFoundException("Booking not found with id: " + eventPayload.getBookingId());
            }
        }
    }

    @Transactional
    public BookingData cancelBooking(UUID bookingId) {
        Optional<BookingEntity> bookingEntity = bookingRepository.findByIdForUpdate(bookingId);
        if (bookingEntity.isPresent()){
            BookingEntity booking = bookingEntity.get();
            if (booking.getState() == BookingState.CONFIRMED || booking.getState() == BookingState.PENDING_PAYMENT) {

                Optional<TripEntity> tripEntity = tripRepository.findById(booking.getTripId());

                if(tripEntity.isPresent()){
                    TripEntity trip = tripEntity.get();
                    if(!isRefundPossible(trip)){
                        booking.setRefundAmount(new BigDecimal(0));
                        throw new RuntimeException("Booking cannot be cancelled as it is past the refundable period");
                    }
                    else{
                        double cancellationFeePercent = 1 - (trip.getRefundPolicy().getCancellationFeePercent() / 100.0);
                        BigDecimal refundAmount = booking.getPriceAtBooking().multiply(BigDecimal.valueOf(cancellationFeePercent));
                        booking.setRefundAmount(refundAmount);
                        releaseSeats(booking);
                    }
                    booking.setState(BookingState.CANCELLED);
                    booking.setCancelledAt(LocalDateTime.now());
                    bookingRepository.save(booking);
                }
                else {
                    throw new TripNotFoundException("Trip not found");
                }

                return mapToData(booking);
            }
            else {
                throw new BookingCancellationException("Only confirmed or pending payment bookings can be cancelled");
            }
        }
        else {
            throw new BookingNotFoundException("Booking not found with id: " + bookingId);
        }
    }

    private boolean isRefundPossible(TripEntity tripEntity) {
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(tripEntity.getStartDate().minusDays(tripEntity.getRefundPolicy().getRefundableUntilDaysBefore()));
    }

    private TripEntity isBookingPossible(BookingData bookingData) {
        Optional<TripEntity> trip = tripRepository.findById(bookingData.getTripId());
        if(trip.isPresent()){
            TripEntity tripEntity = trip.get();
            if(tripEntity.getStatus() == TripStatus.PUBLISHED && tripEntity.getAvailableSeats() >= bookingData.getNumSeats()) {
                return tripEntity;
            }
            else {
                throw new RuntimeException("Not enough seats available");
            }
        }
        else {
            throw new TripNotFoundException("Trip not found");
        }
    }

    private BookingEntity createPendingBooking(BookingData bookingData,TripEntity tripEntity) {
        BookingEntity bookingEntity = mapToEntity(bookingData);
        bookingEntity.setState(BookingState.PENDING_PAYMENT);
        bookingEntity.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        //calculate price at booking time
        bookingEntity.setPriceAtBooking(tripEntity.getPrice().multiply(BigDecimal.valueOf(bookingData.getNumSeats())));

        return bookingRepository.save(bookingEntity);
    }

    private void updateTripSeats(TripEntity tripEntity, int seatsToAdjust) {
        tripEntity.setAvailableSeats(Math.max(0, tripEntity.getAvailableSeats() - seatsToAdjust));
        tripRepository.save(tripEntity);
    }

    private void publishBookingCreatedEvent(UUID bookingId) {
        eventPublisher.publishEvent(new BookingCreatedEvent(bookingId));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    protected void handleBookingCreatedEvent(BookingCreatedEvent event) {
        BookingEntity booking = bookingRepository.findById(event.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + event.getBookingId()));
        initiatePayment(booking);
    }

    /**
     * Initiates payment for a booking by sending a request to an external payment service.
     * Treats payment as successful if the request is sent successfully.
     *
     * @param booking the booking for which to initiate payment
     */
    private void initiatePayment(BookingEntity booking) {

        RestTemplate restTemplate = new RestTemplate();
        String paymentServiceUrl = "https://external-payment-service/api/payments";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                paymentServiceUrl,
                booking,
                String.class
            );
            // Treat as OK if request sent (status 2xx)
            response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            // Log error or handle as needed
            log.error("Failed to initiate payment for booking id: " + booking.getId(), e);
        }
    }

    public void releaseSeats(BookingEntity bookingEntity) {
        Optional<TripEntity> trip = tripRepository.findById(bookingEntity.getTripId());
        if(trip.isPresent()){
            TripEntity tripEntity = trip.get();
            tripEntity.setAvailableSeats(tripEntity.getAvailableSeats() + bookingEntity.getNumSeats());
            tripRepository.save(tripEntity);
        }
        else {
            throw new TripNotFoundException("Trip not found");
        }
    }

    private BookingEntity mapToEntity(BookingData bookingData) {
        return BookingEntity.builder()
                .tripId(bookingData.getTripId())
                .userId(bookingData.getUserId())
                .numSeats(bookingData.getNumSeats())
                .priceAtBooking(bookingData.getPriceAtBooking())
                .paymentReference(bookingData.getPaymentReference())
                .createdAt(LocalDateTime.now())
                .refundAmount(bookingData.getRefundAmount())
                .build();
    }

    private BookingData mapToData(BookingEntity bookingEntity) {
        return BookingData.builder()
                .id(bookingEntity.getId())
                .tripId(bookingEntity.getTripId())
                .userId(bookingEntity.getUserId())
                .numSeats(bookingEntity.getNumSeats())
                .state(bookingEntity.getState())
                .priceAtBooking(bookingEntity.getPriceAtBooking())
                .paymentReference(bookingEntity.getPaymentReference())
                .createdAt(bookingEntity.getCreatedAt())
                .expiresAt(bookingEntity.getExpiresAt())
                .cancelledAt(bookingEntity.getCancelledAt())
                .refundAmount(bookingEntity.getRefundAmount())
                .idempotencyKey(bookingEntity.getIdempotencyKey())
                .updatedAt(bookingEntity.getUpdatedAt())
                .build();
    }
}
