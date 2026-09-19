package com.picnic.routing.client;

import java.util.UUID;

/**
 * Synchronous HTTP client for the Routing service.
 */
public interface RouteClient {

    boolean hasRouteCapacity(UUID warehouseId, UUID slotId);
}
