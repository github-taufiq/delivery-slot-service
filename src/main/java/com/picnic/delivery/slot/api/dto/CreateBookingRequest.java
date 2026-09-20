package com.picnic.delivery.slot.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateBookingRequest(
        @NotNull UUID orderId,
        @NotNull UUID customerId,
        @NotNull UUID slotId) {
}
