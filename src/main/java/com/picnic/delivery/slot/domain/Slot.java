package com.picnic.delivery.slot.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Slot(
        UUID slotId,
        String postcode,
        UUID warehouseId,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        int totalCapacity,
        int remainingCapacity) {
}
