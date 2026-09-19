package com.picnic.customer.client;

import java.util.UUID;

/**
 * Synchronous HTTP client for the Customer service.
 */
public interface CustomerClient {

    Customer getCustomer(UUID customerId);
}
