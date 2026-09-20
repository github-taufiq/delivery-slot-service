package com.picnic.delivery.slot.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RescheduleBookingRequest(
        @NotNull UUID newSlotId) {
}
