# GoTyolo Backend

A Spring Boot backend service for managing travel trip bookings with seat reservation, payment processing, and booking lifecycle management.

---

## Overview

GoTyolo is a trip booking system that allows users to:
- Browse available trips
- Book seats on trips  
- Process payments via webhook integration
- Cancel bookings with refund calculation
- Track trip metrics and at-risk trips


## Tech Stack

- **Framework**: Spring Boot 4.0.3
- **Language**: Java 17
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Maven
- **Containerization**: Docker / Docker Compose

---

## Key Components

- **BookingService**: Core booking lifecycle management
- **TripService**: Trip CRUD and metrics
- **BookingExpiryJob**: Background scheduler for expiring pending bookings
- **PaymentController**: Webhook endpoint for payment events

---

## API Endpoints

### Trips
| Method | Endpoint              | Description              |
|--------|-----------------------|--------------------------|
| GET    | `/trips`              | List all trips           |
| GET    | `/trips/{id}`         | Get trip by ID           |
| POST   | `/trips/create`       | Create a new trip        |
| GET    | `/trips/{id}/metrics` | Get trip booking metrics |
| GET    | `/trips/at-risk`      | List at-risk trips       |

### Bookings
| Method | Endpoint                | Description        |
|--------|-------------------------|--------------------|
| POST   | `/bookings/create`      | Create a booking   |
| GET    | `/bookings/{id}`        | Get booking by ID  |
| POST   | `/bookings/{id}/cancel` | Cancel a booking   |

### Payments
| Method | Endpoint            | Description           |
|--------|---------------------|-----------------------|
| POST   | `/payments/webhook` | Payment event webhook |

---

## Running the Application

### Prerequisites
- Java 17+
- PostgreSQL 13+
- Maven 3.8+
- Docker (optional)

### Run with Maven

```bash
./mvnw spring-boot:run
```

### Run with Docker

```bash
docker-compose up
```

This will start both the application and PostgreSQL database.

---

## Seed Data

The application comes pre-loaded with sample data for immediate use after startup.

### Automatic Seeding (SQL)

On startup, the `data.sql` script automatically populates:

- **5 Sample Trips:**
  - Tropical Beach Getaway (Maldives) - Popular trip
  - Swiss Alps Adventure (Switzerland) - Nearly fully booked
  - Tokyo City Explorer (Japan) - At-risk trip (low bookings)
  - African Safari Experience (Kenya) - Future trip
  - Northern Lights Expedition (Iceland) - Draft status

- **15 Sample Bookings:**
  - 7 Confirmed bookings
  - 2 Pending payment bookings
  - 3 Cancelled bookings (with refund history)
  - 3 Expired bookings

- **10 Payment Events** for confirmed and cancelled bookings


---

## Documentation

For detailed technical documentation, see:

- [Engineering Proposal](ENGINEERING_PROPOSAL.md) - System design, booking lifecycle, transaction handling, and architecture decisions

