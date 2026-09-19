package com.picnic.delivery.slot.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingResponse(
        UUID bookingId,
        UUID orderId,
        UUID slotId,
        String state,
        OffsetDateTime slotStartAt) {
}
