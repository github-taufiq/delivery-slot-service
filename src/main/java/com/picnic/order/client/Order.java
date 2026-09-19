package com.picnic.order.client;

import java.util.UUID;

public record Order(UUID orderId, UUID customerId, String deliveryPostcode) {
}
