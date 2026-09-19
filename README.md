# delivery-slot-service

Delivery slot lookup, booking and rescheduling for the Store team.

Each warehouse pre-generates delivery slots for each postcode every day with a fixed
capacity. Customers retrieve the available slots for a postcode and date, book one for
an existing order, and reschedule an existing booking.

## API

| Method | Path                     | Purpose                                    |
| ------ | ------------------------ | ------------------------------------------ |
| `GET`  | `/slots`                 | Available slots for `postcode` and `date`  |
| `POST` | `/bookings`              | Book a slot for an existing order          |
| `PUT`  | `/bookings/{id}`         | Move an existing booking to another slot   |

Slot generation, cancellation and payment are owned elsewhere and are out of scope here.

## Business rules

- Only slots with `remaining_capacity > 0` are offered.
- A booking must be made at least **2 hours** before the slot starts.
- The `date` on `GET /slots` is the **warehouse's local date**; every timestamp the API
  returns is **UTC**.
- Each postcode maps to exactly one warehouse.
- The customer must own the order.
- An order may have at most one `ACTIVE` booking.
- Reserving a booking and decrementing slot capacity must be atomic.
- Rescheduling must atomically release the old slot, reserve the new slot and update the
  booking.
- Notifications are sent after booking and rescheduling.

## Stack

Java 21, Spring Boot 3.3, PostgreSQL, jOOQ, Flyway, JUnit 5, AssertJ, Mockito.

`OrderClient`, `CustomerClient`, `NotificationClient` and `RouteClient` are synchronous
HTTP clients to services owned by other teams. The authentication principal carries the
caller's `customer_id` — see `SecurityUtils.currentCustomerId`.

jOOQ codegen output under `com.picnic.delivery.jooq` is checked in so the service builds
without a live database.

## Build

```bash
mvn clean test
```

---

### About this repository

This is a practice repository for a code review interview. `main` holds the service
skeleton; the branch `feature/slot-booking` contains a draft implementation submitted for
review. The implementation is deliberately imperfect — reviewing it is the exercise.
