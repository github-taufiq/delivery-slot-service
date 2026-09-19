package com.picnic.customer.client;

import java.util.UUID;

public record Customer(UUID customerId, String email) {
}
