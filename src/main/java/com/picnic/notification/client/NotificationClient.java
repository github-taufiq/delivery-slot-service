package com.picnic.notification.client;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Synchronous HTTP client for the Notification service.
 */
public interface NotificationClient {

    void sendBookingConfirmation(String email, UUID bookingId);

    void sendRescheduleConfirmation(String email, UUID bookingId, OffsetDateTime newSlotStart);
}
