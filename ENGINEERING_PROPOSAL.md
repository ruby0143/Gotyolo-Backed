# GoTyolo Engineering Proposal

## 1. Booking Lifecycle Diagram and State Transitions

### Booking States
The booking system uses a finite state machine with the following states defined in `BookingState` enum:

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           BOOKING LIFECYCLE DIAGRAM                              │
└─────────────────────────────────────────────────────────────────────────────────┘

                              ┌──────────────────┐
                              │  User Initiates  │
                              │     Booking      │
                              └────────┬─────────┘
                                       │
                                       ▼
                              ┌──────────────────┐
                              │ Check Seat       │
                              │ Availability     │
                              └────────┬─────────┘
                                       │
                    ┌──────────────────┼──────────────────┐
                    │ Seats Available  │                  │ No Seats
                    ▼                  │                  ▼
           ┌────────────────┐          │         ┌────────────────┐
           │ Reserve Seats  │          │         │ Throw Runtime  │
           │ (Decrement     │          │         │ Exception      │
           │ available_seats)          │         └────────────────┘
           └────────┬───────┘          │
                    │                  │
                    ▼                  │
           ┌────────────────┐          │
           │ PENDING_PAYMENT│◄─────────┘
           │ (15 min TTL)   │
           └────────┬───────┘
                    │
     ┌──────────────┼──────────────┬───────────────────┐
     │              │              │                   │
     │ Payment      │ Payment      │ No Payment        │ User Cancels
     ��� Success      │ Failed       │ (TTL Expired)     │
     ▼              ▼              ▼                   ▼
┌──────────┐  ┌──────────┐   ┌──────────┐       ┌──────────┐
│CONFIRMED │  │ EXPIRED  │   │ EXPIRED  │       │CANCELLED │
└────┬─────┘  └────┬─────┘   └────┬─────┘       └────┬─────┘
     │             │              │                  │
     │             │              │                  │
     │             ▼              ▼                  ▼
     │        ┌────────────────────────────────────────┐
     │        │ Release Reserved Seats                 │
     │        │ (Increment available_seats)            │
     │        └────────────────────────────────────────┘
     │
     │ User Cancels (within refund policy)
     ▼
┌──────────┐
│CANCELLED │ → Release Seats + Calculate Refund
└──────────┘
```

### State Transition Matrix

| Current State      | Event                 | Next State        | Side Effects                           |
|--------------------|-----------------------|-------------------|----------------------------------------|
| (none)             | Create Booking        | PENDING_PAYMENT   | Decrement `available_seats`, set TTL   |
| PENDING_PAYMENT    | Payment Success       | CONFIRMED         | Store payment event                    |
| PENDING_PAYMENT    | Payment Failed        | EXPIRED           | Release seats                          |
| PENDING_PAYMENT    | TTL Expired           | EXPIRED           | Release seats                          |
| PENDING_PAYMENT    | User Cancels          | CANCELLED         | Release seats, calculate refund        |
| CONFIRMED          | User Cancels          | CANCELLED         | Release seats, calculate refund        |

---

## 2. Overbooking Prevention Strategy

### Current Implementation: Pessimistic Locking

The system uses **pessimistic locking** via `SELECT ... FOR UPDATE` to prevent race conditions:

```java
// BookingRepository.java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query(value = "SELECT b FROM BookingEntity b WHERE b.id = :id")
Optional<BookingEntity> findByIdForUpdate(UUID id);
```

### Concurrency Strategy

1. **Transaction Isolation**: All booking operations use `@Transactional` annotations ensuring ACID compliance.

2. **Optimistic Locking on Trip**: The `TripEntity` has a `@Version` field that provides optimistic locking:
   ```java
   @Version
   private Long version;
   ```
   This prevents concurrent modifications to the same trip (including seat count updates).

3. **Atomic Seat Reservation**: Within `createBooking()`:
   - Check seat availability
   - Decrement `available_seats` immediately
   - Create booking in `PENDING_PAYMENT` state
   - All within a single transaction


## 3. Database Transactions Usage

### Transaction Boundaries

| Method                          | Annotation       | Purpose                                                  |
|---------------------------------|------------------|----------------------------------------------------------|
| `createBooking()`               | `@Transactional` | Atomic: Check + Reserve + Create Booking                 |
| `validateAndUpdateBookingState()` | `@Transactional` | Atomic: Validate Payment + Update State + Release Seats  |
| `cancelBooking()`               | `@Transactional` | Atomic: Update State + Release Seats + Calculate Refund  |

### Transaction Flow for Booking Creation

```
BEGIN TRANSACTION
  ├── SELECT trip WHERE id = ?
  ├── Validate seats available
  ├── UPDATE trips SET available_seats = (available_seats - numSeats)
  ├── INSERT INTO bookings (state = PENDING_PAYMENT, expires_at = NOW + 15min)
  └── COMMIT
AFTER COMMIT
  └── Publish BookingCreatedEvent → Trigger Payment Initiation
```

### Idempotency Protection

Payment events use idempotency keys stored in `payment_events` table to prevent duplicate processing:

```java
Optional<PaymentEventEntity> paymentEvent = paymentEventRepository.findById(eventPayload.getIdempotencyKey());
if (paymentEvent.isPresent()) {
    return null; // Already processed
}
```

---

## 4. Booking Auto-Expiry Implementation

### Current Approach: Spring Scheduler (Background Job)

The system uses `@Scheduled` annotation with Spring's scheduling framework:

```java
@EnableScheduling  // Enabled in GoTyoloApplication.java

@Component
public class BookingExpiryJob {
    
    @Scheduled(fixedRate = 60000)  // Every 1 minute
    public void expirePendingBookings() {
        List<BookingEntity> expiredBookings = bookingRepository
            .findByStateAndExpiry(BookingState.PENDING_PAYMENT, LocalDateTime.now());
        
        for (BookingEntity booking : expiredBookings) {
            booking.setState(BookingState.EXPIRED);
            bookingService.releaseSeats(booking);
        }
        bookingRepository.saveAll(expiredBookings);
    }
}
```

### Why This Approach?

| Approach               | Pros                                          | Cons                                          |
|------------------------|-----------------------------------------------|-----------------------------------------------|
| **Spring Scheduler ✓** | Simple, in-app, no external dependencies      | Single instance only, not distributed         |
| Database Job           | DB-level trigger, no app dependency           | Complex, DB-specific, harder to debug         |
| Redis TTL + Events     | Real-time expiry, distributed                 | Additional infrastructure, complexity         |
| Lazy Expiry Check      | No background job needed                      | Stale data until accessed, seats not released |

### Current Trade-offs

- **Chosen**: Simplicity and maintainability over real-time precision
- **15-minute TTL + 1-minute polling**: Max delay of ~1 minute for seat release
- **Batch processing**: Efficient for moderate traffic

---

## 5. Trade-offs Considered

### Design Decisions

| Decision                           | Trade-off                                                    | Justification                                          |
|------------------------------------|--------------------------------------------------------------|--------------------------------------------------------|
| Pessimistic locking                | Lower throughput vs. data consistency                        | Prevents overselling at the cost of performance        |
| Denormalized `available_seats`     | Storage overhead vs. query performance                       | O(1) availability check vs. O(n) aggregation           |
| 15-minute payment window           | User experience vs. seat utilization                         | Balance between giving users time and releasing seats  |
| In-app scheduler vs. DB triggers   | Simplicity vs. distributed capability                        | Suitable for single-instance deployment                |
| Optimistic locking on Trip         | Retry overhead vs. lock contention                           | Better for read-heavy workloads                        |

### Why This Approach is Suitable

1. **Spring Boot Ecosystem**: Leverages native Spring features (`@Transactional`, `@Scheduled`, `@Version`)
2. **PostgreSQL Compatibility**: Uses standard JPA/Hibernate with PostgreSQL dialect
3. **Operational Simplicity**: No external job schedulers or message queues required
4. **Testability**: All components are unit-testable with standard Spring Test framework
5. **Scalability Path**: Clear upgrade path to distributed scheduling (Quartz, ShedLock) when needed

---

## 6. Design Justification: Denormalized `available_seats` Field

### Overview

The `TripEntity` stores `available_seats` as a pre-calculated field rather than dynamically computing it from bookings. This is a deliberate denormalization decision.

### Why Denormalization is Used

| Aspect                  | Normalized (Dynamic Calculation)              | Denormalized (Stored Field)           |
|-------------------------|-----------------------------------------------|---------------------------------------|
| **Query Performance**   | O(n) - JOIN + GROUP BY on every read          | O(1) - Direct field access            |
| **Booking Check**       | Complex subquery for each booking request     | Simple `WHERE available_seats >= ?`   |
| **Read Frequency**      | Calculated on every trip list/detail request  | Pre-computed, instant retrieval       |
| **Write Frequency**     | No writes needed                              | Update on booking/cancellation        |

**Decision**: Given that trip availability is checked frequently (every booking attempt, trip listing, seat display), the O(1) read performance justifies the denormalization.

### Risks Involved

| Risk                           | Description                                              | Severity |
|--------------------------------|----------------------------------------------------------|----------|
| **Data Inconsistency**         | `available_seats` may drift from actual booking count    | HIGH     |
| **Race Conditions**            | Concurrent updates may cause incorrect counts            | HIGH     |
| **Orphaned Reservations**      | System crash between seat decrement and booking create   | MEDIUM   |
| **Manual DB Changes**          | Direct SQL updates may bypass seat count logic           | LOW      |

### How Consistency is Ensured

1. **Transactional Updates**: All seat modifications occur within `@Transactional` methods ensuring atomicity - either all changes commit or all rollback.

2. **Optimistic Locking**: The `@Version` field on `TripEntity` prevents lost updates when concurrent transactions modify the same trip.

3. **Seat Release on Expiry/Cancellation**: The `releaseSeats()` method properly restores seat counts when bookings expire or are cancelled.

4. **Database Constraint (Recommended)**: Adding a CHECK constraint at the database level prevents negative seat values.

5. **Reconciliation Monitoring**: Periodic queries can detect and alert on any drift between stored `available_seats` and calculated values from booking records.



