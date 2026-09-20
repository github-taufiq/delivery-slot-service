package com.picnic.delivery.slot.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SlotResponse(
        UUID slotId,
        String postcode,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        int totalCapacity,
        int remainingCapacity) {
}
