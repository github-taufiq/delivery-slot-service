package com.picnic.delivery.config;

import java.util.UUID;

/**
 * The principal the gateway puts on every authenticated request.
 */
public record CustomerPrincipal(UUID customerId, String email) {
}
