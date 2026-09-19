package com.picnic.order.client;

import java.util.UUID;

/**
 * Synchronous HTTP client for the Order service, owned by the Orders team.
 */
public interface OrderClient {

    Order getOrder(UUID orderId);
}
