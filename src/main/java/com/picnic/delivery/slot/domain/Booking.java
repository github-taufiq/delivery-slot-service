package com.picnic.delivery.slot.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Booking(
        UUID bookingId,
        UUID orderId,
        UUID customerId,
        UUID slotId,
        BookingState state,
        OffsetDateTime createdAt) {
}
