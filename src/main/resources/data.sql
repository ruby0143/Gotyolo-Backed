-- GoTyolo Seed Data
-- This script populates the database with sample trips, bookings, and payment events
-- Executed automatically on application startup when spring.sql.init.mode=always

-- =====================================================
-- TRIPS (5 Sample Trips with various configurations)
-- =====================================================

-- Trip 1: Beach Getaway - Popular trip with many bookings (starts in 30 days)
INSERT INTO trips (id, title, destination, start_date, end_date, price, max_capacity, available_seats, status, refundable_until_days_before, cancellation_fee_percent, created_at, updated_at, version)
VALUES (
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'Tropical Beach Getaway',
    'Maldives',
    CURRENT_TIMESTAMP + INTERVAL '30 days',
    CURRENT_TIMESTAMP + INTERVAL '37 days',
    1299.99,
    20,
    12,
    'PUBLISHED',
    7,
    15,
    CURRENT_TIMESTAMP - INTERVAL '60 days',
    CURRENT_TIMESTAMP,
    0
) ON CONFLICT (id) DO NOTHING;

-- Trip 2: Mountain Adventure - Upcoming trip (starts in 14 days)
INSERT INTO trips (id, title, destination, start_date, end_date, price, max_capacity, available_seats, status, refundable_until_days_before, cancellation_fee_percent, created_at, updated_at, version)
VALUES (
    'b2c3d4e5-f6a7-8901-bcde-f23456789012',
    'Swiss Alps Adventure',
    'Switzerland',
    CURRENT_TIMESTAMP + INTERVAL '14 days',
    CURRENT_TIMESTAMP + INTERVAL '21 days',
    2499.99,
    15,
    3,
    'PUBLISHED',
    10,
    20,
    CURRENT_TIMESTAMP - INTERVAL '45 days',
    CURRENT_TIMESTAMP,
    0
) ON CONFLICT (id) DO NOTHING;

-- Trip 3: City Explorer - Starting soon, at-risk trip (starts in 5 days, low bookings)
INSERT INTO trips (id, title, destination, start_date, end_date, price, max_capacity, available_seats, status, refundable_until_days_before, cancellation_fee_percent, created_at, updated_at, version)
VALUES (
    'c3d4e5f6-a7b8-9012-cdef-345678901234',
    'Tokyo City Explorer',
    'Japan',
    CURRENT_TIMESTAMP + INTERVAL '5 days',
    CURRENT_TIMESTAMP + INTERVAL '10 days',
    1899.99,
    25,
    22,
    'PUBLISHED',
    3,
    25,
    CURRENT_TIMESTAMP - INTERVAL '30 days',
    CURRENT_TIMESTAMP,
    0
) ON CONFLICT (id) DO NOTHING;

-- Trip 4: Safari Experience - Future trip (starts in 60 days)
INSERT INTO trips (id, title, destination, start_date, end_date, price, max_capacity, available_seats, status, refundable_until_days_before, cancellation_fee_percent, created_at, updated_at, version)
VALUES (
    'd4e5f6a7-b8c9-0123-defa-456789012345',
    'African Safari Experience',
    'Kenya',
    CURRENT_TIMESTAMP + INTERVAL '60 days',
    CURRENT_TIMESTAMP + INTERVAL '67 days',
    3499.99,
    12,
    8,
    'PUBLISHED',
    14,
    10,
    CURRENT_TIMESTAMP - INTERVAL '20 days',
    CURRENT_TIMESTAMP,
    0
) ON CONFLICT (id) DO NOTHING;

-- Trip 5: Draft Trip - Not yet published
INSERT INTO trips (id, title, destination, start_date, end_date, price, max_capacity, available_seats, status, refundable_until_days_before, cancellation_fee_percent, created_at, updated_at, version)
VALUES (
    'e5f6a7b8-c9d0-1234-efab-567890123456',
    'Northern Lights Expedition',
    'Iceland',
    CURRENT_TIMESTAMP + INTERVAL '90 days',
    CURRENT_TIMESTAMP + INTERVAL '97 days',
    2799.99,
    18,
    18,
    'DRAFT',
    21,
    5,
    CURRENT_TIMESTAMP - INTERVAL '5 days',
    CURRENT_TIMESTAMP,
    0
) ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- BOOKINGS (15 Sample Bookings in various states)
-- =====================================================

-- User IDs for realistic data
-- User 1: 11111111-1111-1111-1111-111111111111
-- User 2: 22222222-2222-2222-2222-222222222222
-- User 3: 33333333-3333-3333-3333-333333333333
-- User 4: 44444444-4444-4444-4444-444444444444
-- User 5: 55555555-5555-5555-5555-555555555555

-- ===== CONFIRMED BOOKINGS =====

-- Booking 1: Confirmed booking for Beach Getaway (2 seats)
INSERT INTO bookings (id, trip_id, user_id, num_seats, state, price_at_booking, payment_reference, created_at, expires_at, cancelled_at, refund_amount, idempotency_key, updated_at)
VALUES (
    'f1234567-89ab-cdef-0123-456789abcdef',
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    '11111111-1111-1111-1111-111111111111',
    2,
    'CONFIRMED',
    2599.98,
    'PAY-BEACH-001-SUCCESS',
    CURRENT_TIMESTAMP - INTERVAL '45 days',
    CURRENT_TIMESTAMP - INTERVAL '45 days' + INTERVAL '15 minutes',
    NULL,
    NULL,
    'IDEM-BEACH-001',
    CURRENT_TIMESTAMP - INTERVAL '45 days'
) ON CONFLICT (id) DO NOTHING;

-- Booking 2: Confirmed booking for Beach Getaway (1 seat)
INSERT INTO bookings (id, trip_id, user_id, num_seats, state, price_at_booking, payment_reference, created_at, expires_at, cancelled_at, refund_amount, idempotency_key, updated_at)
VALUES (
    'f2345678-9abc-def0-1234-56789abcdef0',
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    '22222222-2222-2222-2222-222222222222',
    1,
    'CONFIRMED',
    1299.99,
    'PAY-BEACH-002-SUCCESS',
    CURRENT_TIMESTAMP - INTERVAL '30 days',
    CURRENT_TIMESTAMP - INTERVAL '30 days' + INTERVAL '15 minutes',
    NULL,
    NULL,
    'IDEM-BEACH-002',
    CURRENT_TIMESTAMP - INTERVAL '30 days'
) ON CONFLICT (id) DO NOTHING;

-- Booking 3: Confirmed booking for Swiss Alps (3 seats)
INSERT INTO bookings (id, trip_id, user_id, num_seats, state, price_at_booking, payment_reference, created_at, expires_at, cancelled_at, refund_amount, idempotency_key, updated_at)
VALUES (
    'f3456789-abcd-ef01-2345-6789abcdef01',
    'b2c3d4e5-f6a7-8901-bcde-f23456789012',
    '33333333-3333-3333-3333-333333333333',
    3,
    'CONFIRMED',
    7499.97,
    'PAY-ALPS-001-SUCCESS',
    CURRENT_TIMESTAMP - INTERVAL '20 days',
    CURRENT_TIMESTAMP - INTERVAL '20 days' + INTERVAL '15 minutes',
    NULL,
    NULL,
    'IDEM-ALPS-001',
    CURRENT_TIMESTAMP - INTERVAL '20 days'
) ON CONFLICT (id) DO NOTHING;

-- Booking 4: Confirmed booking for Swiss Alps (5 seats - group booking)
INSERT INTO bookings (id, trip_id, user_id, num_seats, state, price_at_booking, payment_reference, created_at, expires_at, cancelled_at, refund_amount, idempotency_key, updated_at)
VALUES (
    'f4567890-bcde-f012-3456-789abcdef012',
    'b2c3d4e5-f6a7-8901-bcde-f23456789012',
    '44444444-4444-4444-4444-444444444444',
    5,
    'CONFIRMED',
    12499.95,
    'PAY-ALPS-002-SUCCESS',
    CURRENT_TIMESTAMP - INTERVAL '15 days',
    CURRENT_TIMESTAMP - INTERVAL '15 days' + INTERVAL '15 minutes',
    NULL,
    NULL,
    'IDEM-ALPS-002',
    CURRENT_TIMESTAMP - INTERVAL '15 days'
) ON CONFLICT (id) DO NOTHING;

-- Booking 5: Confirmed booking for Safari (4 seats)
INSERT INTO bookings (id, trip_id, user_id, num_seats, state, price_at_booking, payment_reference, created_at, expires_at, cancelled_at, refund_amount, idempotency_key, updated_at)
VALUES (
    'f5678901-cdef-0123-4567-89abcdef0123',
    'd4e5f6a7-b8c9-0123-defa-456789012345',
    '11111111-1111-1111-1111-111111111111',
    4,
    'CONFIRMED',
    13999.96,
    'PAY-SAFARI-001-SUCCESS',
    CURRENT_TIMESTAMP - INTERVAL '10 days',
    CURRENT_TIMESTAMP - INTERVAL '10 days' + INTERVAL '15 minutes',
    NULL,
    NULL,
    'IDEM-SAFARI-001',
    CURRENT_TIMESTAMP - INTERVAL '10 days'
) ON CONFLICT (id) DO NOTHING;

-- Booking 6: Confirmed booking for Tokyo (2 seats)
INSERT INTO bookings (id, trip_id, user_id, num_seats, state, price_at_booking, payment_reference, created_at, expires_at, cancelled_at, refund_amount, idempotency_key, updated_at)
VALUES (
    'f6789012-def0-1234-5678-9abcdef01234',
    'c3d4e5f6-a7b8-9012-cdef-345678901234',
    '55555555-5555-5555-5555-555555555555',
    2,
    'CONFIRMED',
    3799.98,
    'PAY-TOKYO-001-SUCCESS',
    CURRENT_TIMESTAMP - INTERVAL '8 days',
    CURRENT_TIMESTAMP - INTERVAL '8 days' + INTERVAL '15 minutes',
    NULL,
    NULL,
    'IDEM-TOKYO-001',
    CURRENT_TIMESTAMP - INTERVAL '8 days'
) ON CONFLICT (id) DO NOTHING;

-- Booking 7: Confirmed booking for Tokyo (1 seat)
INSERT INTO bookings (id, trip_id, user_id, num_seats, state, price_at_booking, payment_reference, created_at, expires_at, cancelled_at, refund_amount, idempotency_key, updated_at)
VALUES (
    'f7890123-ef01-2345-6789-abcdef012345',
    'c3d4e5f6-a7b8-9012-cdef-345678901234',
    '22222222-2222-2222-2222-222222222222',
    1,
    'CONFIRMED',
    1899.99,
    'PAY-TOKYO-002-SUCCESS',
    CURRENT_TIMESTAMP - INTERVAL '3 days',
    CURRENT_TIMESTAMP - INTERVAL '3 days' + INTERVAL '15 minutes',
    NULL,
    NULL,
    'IDEM-TOKYO-002',
    CURRENT_TIMESTAMP - INTERVAL '3 days'
) ON CONFLICT (id) DO NOTHING;

-- ===== PENDING PAYMENT BOOKINGS =====

-- Booking 8: Pending payment for Beach Getaway (expires in 10 minutes)
INSERT INTO bookings (id, trip_id, user_id, num_seats, state, price_at_booking, payment_reference, created_at, expires_at, cancelled_at, refund_amount, idempotency_key, updated_at)
VALUES (
    'f8901234-0123-4567-89ab-cdef01234567',
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    '44444444-4444-4444-4444-444444444444',
    2,
    'PENDING_PAYMENT',
    2599.98,
    NULL,
    CURRENT_TIMESTAMP - INTERVAL '5 minutes',
    CURRENT_TIMESTAMP + INTERVAL '10 minutes',
    NULL,
    NULL,
    'IDEM-BEACH-PENDING-001',
    CURRENT_TIMESTAMP - INTERVAL '5 minutes'
) ON CONFLICT (id) DO NOTHING;

-- Booking 9: Pending payment for Swiss Alps (expires in 12 minutes)
INSERT INTO bookings (id, trip_id, user_id, num_seats, state, price_at_booking, payment_reference, created_at, expires_at, cancelled_at, refund_amount, idempotency_key, updated_at)
VALUES (
    'f9012345-1234-5678-9abc-def012345678',
    'b2c3d4e5-f6a7-8901-bcde-f23456789012',
    '55555555-5555-5555-5555-555555555555',
    2,
    'PENDING_PAYMENT',
    4999.98,
    NULL,
    CURRENT_TIMESTAMP - INTERVAL '3 minutes',
    CURRENT_TIMESTAMP + INTERVAL '12 minutes',
    NULL,
    NULL,
    'IDEM-ALPS-PENDING-001',
    CURRENT_TIMESTAMP - INTERVAL '3 minutes'
) ON CONFLICT (id) DO NOTHING;

-- ===== CANCELLED BOOKINGS =====

-- Booking 10: Cancelled booking for Beach Getaway (full refund - cancelled early)
INSERT INTO bookings (id, trip_id, user_id, num_seats, state, price_at_booking, payment_reference, created_at, expires_at, cancelled_at, refund_amount, idempotency_key, updated_at)
VALUES (
    'fa123456-2345-6789-abcd-ef0123456789',
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    '33333333-3333-3333-3333-333333333333',
    1,
    'CANCELLED',
    1299.99,
    'PAY-BEACH-CANCEL-001',
    CURRENT_TIMESTAMP - INTERVAL '50 days',
    CURRENT_TIMESTAMP - INTERVAL '50 days' + INTERVAL '15 minutes',
    CURRENT_TIMESTAMP - INTERVAL '40 days',
    1104.99,
    'IDEM-BEACH-CANCEL-001',
    CURRENT_TIMESTAMP - INTERVAL '40 days'
) ON CONFLICT (id) DO NOTHING;

-- Booking 11: Cancelled booking for Swiss Alps (partial refund - cancellation fee applied)
INSERT INTO bookings (id, trip_id, user_id, num_seats, state, price_at_booking, payment_reference, created_at, expires_at, cancelled_at, refund_amount, idempotency_key, updated_at)
VALUES (
    'fb234567-3456-789a-bcde-f01234567890',
    'b2c3d4e5-f6a7-8901-bcde-f23456789012',
    '11111111-1111-1111-1111-111111111111',
    2,
    'CANCELLED',
    4999.98,
    'PAY-ALPS-CANCEL-001',
    CURRENT_TIMESTAMP - INTERVAL '35 days',
    CURRENT_TIMESTAMP - INTERVAL '35 days' + INTERVAL '15 minutes',
    CURRENT_TIMESTAMP - INTERVAL '25 days',
    3999.98,
    'IDEM-ALPS-CANCEL-001',
    CURRENT_TIMESTAMP - INTERVAL '25 days'
) ON CONFLICT (id) DO NOTHING;

-- Booking 12: Cancelled booking for Safari (late cancellation - lower refund)
INSERT INTO bookings (id, trip_id, user_id, num_seats, state, price_at_booking, payment_reference, created_at, expires_at, cancelled_at, refund_amount, idempotency_key, updated_at)
VALUES (
    'fc345678-4567-89ab-cdef-012345678901',
    'd4e5f6a7-b8c9-0123-defa-456789012345',
    '22222222-2222-2222-2222-222222222222',
    1,
    'CANCELLED',
    3499.99,
    'PAY-SAFARI-CANCEL-001',
    CURRENT_TIMESTAMP - INTERVAL '25 days',
    CURRENT_TIMESTAMP - INTERVAL '25 days' + INTERVAL '15 minutes',
    CURRENT_TIMESTAMP - INTERVAL '5 days',
    3149.99,
    'IDEM-SAFARI-CANCEL-001',
    CURRENT_TIMESTAMP - INTERVAL '5 days'
) ON CONFLICT (id) DO NOTHING;

-- ===== EXPIRED BOOKINGS =====

-- Booking 13: Expired booking for Beach Getaway (payment timeout)
INSERT INTO bookings (id, trip_id, user_id, num_seats, state, price_at_booking, payment_reference, created_at, expires_at, cancelled_at, refund_amount, idempotency_key, updated_at)
VALUES (
    'fd456789-5678-9abc-def0-123456789012',
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    '44444444-4444-4444-4444-444444444444',
    1,
    'EXPIRED',
    1299.99,
    NULL,
    CURRENT_TIMESTAMP - INTERVAL '2 days',
    CURRENT_TIMESTAMP - INTERVAL '2 days' + INTERVAL '15 minutes',
    NULL,
    NULL,
    'IDEM-BEACH-EXPIRED-001',
    CURRENT_TIMESTAMP - INTERVAL '2 days' + INTERVAL '15 minutes'
) ON CONFLICT (id) DO NOTHING;

-- Booking 14: Expired booking for Tokyo (payment timeout)
INSERT INTO bookings (id, trip_id, user_id, num_seats, state, price_at_booking, payment_reference, created_at, expires_at, cancelled_at, refund_amount, idempotency_key, updated_at)
VALUES (
    'fe567890-6789-abcd-ef01-234567890123',
    'c3d4e5f6-a7b8-9012-cdef-345678901234',
    '33333333-3333-3333-3333-333333333333',
    2,
    'EXPIRED',
    3799.98,
    NULL,
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    CURRENT_TIMESTAMP - INTERVAL '1 day' + INTERVAL '15 minutes',
    NULL,
    NULL,
    'IDEM-TOKYO-EXPIRED-001',
    CURRENT_TIMESTAMP - INTERVAL '1 day' + INTERVAL '15 minutes'
) ON CONFLICT (id) DO NOTHING;

-- Booking 15: Expired booking for Safari (payment timeout)
INSERT INTO bookings (id, trip_id, user_id, num_seats, state, price_at_booking, payment_reference, created_at, expires_at, cancelled_at, refund_amount, idempotency_key, updated_at)
VALUES (
    'ff678901-789a-bcde-f012-345678901234',
    'd4e5f6a7-b8c9-0123-defa-456789012345',
    '55555555-5555-5555-5555-555555555555',
    2,
    'EXPIRED',
    6999.98,
    NULL,
    CURRENT_TIMESTAMP - INTERVAL '12 hours',
    CURRENT_TIMESTAMP - INTERVAL '12 hours' + INTERVAL '15 minutes',
    NULL,
    NULL,
    'IDEM-SAFARI-EXPIRED-001',
    CURRENT_TIMESTAMP - INTERVAL '12 hours' + INTERVAL '15 minutes'
) ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- PAYMENT EVENTS (For confirmed bookings)
-- =====================================================

INSERT INTO payment_events (idempotency_key, state, booking_id)
VALUES ('IDEM-BEACH-001', 'SUCCESS', 'f1234567-89ab-cdef-0123-456789abcdef')
ON CONFLICT (idempotency_key) DO NOTHING;

INSERT INTO payment_events (idempotency_key, state, booking_id)
VALUES ('IDEM-BEACH-002', 'SUCCESS', 'f2345678-9abc-def0-1234-56789abcdef0')
ON CONFLICT (idempotency_key) DO NOTHING;

INSERT INTO payment_events (idempotency_key, state, booking_id)
VALUES ('IDEM-ALPS-001', 'SUCCESS', 'f3456789-abcd-ef01-2345-6789abcdef01')
ON CONFLICT (idempotency_key) DO NOTHING;

INSERT INTO payment_events (idempotency_key, state, booking_id)
VALUES ('IDEM-ALPS-002', 'SUCCESS', 'f4567890-bcde-f012-3456-789abcdef012')
ON CONFLICT (idempotency_key) DO NOTHING;

INSERT INTO payment_events (idempotency_key, state, booking_id)
VALUES ('IDEM-SAFARI-001', 'SUCCESS', 'f5678901-cdef-0123-4567-89abcdef0123')
ON CONFLICT (idempotency_key) DO NOTHING;

INSERT INTO payment_events (idempotency_key, state, booking_id)
VALUES ('IDEM-TOKYO-001', 'SUCCESS', 'f6789012-def0-1234-5678-9abcdef01234')
ON CONFLICT (idempotency_key) DO NOTHING;

INSERT INTO payment_events (idempotency_key, state, booking_id)
VALUES ('IDEM-TOKYO-002', 'SUCCESS', 'f7890123-ef01-2345-6789-abcdef012345')
ON CONFLICT (idempotency_key) DO NOTHING;

-- Payment events for cancelled bookings (they were confirmed before cancellation)
INSERT INTO payment_events (idempotency_key, state, booking_id)
VALUES ('IDEM-BEACH-CANCEL-001', 'SUCCESS', 'fa123456-2345-6789-abcd-ef0123456789')
ON CONFLICT (idempotency_key) DO NOTHING;

INSERT INTO payment_events (idempotency_key, state, booking_id)
VALUES ('IDEM-ALPS-CANCEL-001', 'SUCCESS', 'fb234567-3456-789a-bcde-f01234567890')
ON CONFLICT (idempotency_key) DO NOTHING;

INSERT INTO payment_events (idempotency_key, state, booking_id)
VALUES ('IDEM-SAFARI-CANCEL-001', 'SUCCESS', 'fc345678-4567-89ab-cdef-012345678901')
ON CONFLICT (idempotency_key) DO NOTHING;

