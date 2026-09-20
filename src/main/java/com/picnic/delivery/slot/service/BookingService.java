package com.picnic.delivery.slot.service;

import com.picnic.customer.client.Customer;
import com.picnic.customer.client.CustomerClient;
import com.picnic.delivery.slot.api.dto.BookingResponse;
import com.picnic.delivery.slot.api.dto.CreateBookingRequest;
import com.picnic.delivery.slot.api.dto.RescheduleBookingRequest;
import com.picnic.delivery.slot.domain.Booking;
import com.picnic.delivery.slot.domain.BookingState;
import com.picnic.delivery.slot.domain.Slot;
import com.picnic.delivery.slot.repository.BookingRepository;
import com.picnic.delivery.slot.repository.SlotRepository;
import com.picnic.notification.client.NotificationClient;
import com.picnic.order.client.Order;
import com.picnic.order.client.OrderClient;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private static final int BOOKING_CUTOFF_HOURS = 2;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private NotificationClient notificationClient;

    public BookingResponse createBooking(CreateBookingRequest request) {
        Order order = orderClient.getOrder(request.orderId());
        if (order == null) {
            throw new RuntimeException("Order " + request.orderId() + " does not exist");
        }
        if (!order.customerId().equals(request.customerId())) {
            throw new RuntimeException("Order does not belong to this customer");
        }

        Slot slot = slotRepository.findById(request.slotId());

        if (slot.remainingCapacity() <= 0) {
            throw new RuntimeException("Slot " + slot.slotId() + " is fully booked");
        }

        LocalDateTime cutoff = LocalDateTime.now().plusHours(BOOKING_CUTOFF_HOURS);
        if (cutoff.isAfter(slot.startAt().toLocalDateTime())) {
            throw new RuntimeException("Slot starts in less than " + BOOKING_CUTOFF_HOURS + " hours");
        }

        for (Booking existing : bookingRepository.findByOrderId(request.orderId())) {
            if (existing.state() == BookingState.ACTIVE) {
                throw new RuntimeException("Order already has an active booking");
            }
        }

        Booking booking = this.reserveAndInsert(request, slot);

        Customer customer = customerClient.getCustomer(request.customerId());
        notificationClient.sendBookingConfirmation(customer.email(), booking.bookingId());

        log.info("Created booking {} for order {}", booking.bookingId(), request.orderId());

        return toResponse(booking, slot);
    }

    @Transactional
    public Booking reserveAndInsert(CreateBookingRequest request, Slot slot) {
        slotRepository.updateRemainingCapacity(slot.slotId(), slot.remainingCapacity() - 1);

        return bookingRepository.insert(
                request.orderId(),
                request.customerId(),
                slot.slotId(),
                BookingState.ACTIVE);
    }

    @Transactional
    public BookingResponse reschedule(UUID bookingId, RescheduleBookingRequest request) {
        Booking booking = bookingRepository.findById(bookingId);
        Slot newSlot = slotRepository.findById(request.newSlotId());

        if (newSlot.remainingCapacity() <= 0) {
            throw new RuntimeException("Slot " + newSlot.slotId() + " is fully booked");
        }

        Slot oldSlot = slotRepository.findById(booking.slotId());

        slotRepository.updateRemainingCapacity(oldSlot.slotId(), oldSlot.remainingCapacity() + 1);
        slotRepository.updateRemainingCapacity(newSlot.slotId(), newSlot.remainingCapacity() - 1);

        bookingRepository.updateSlot(bookingId, newSlot.slotId());
        bookingRepository.updateState(bookingId, BookingState.RESCHEDULED);

        Customer customer = customerClient.getCustomer(booking.customerId());
        notificationClient.sendRescheduleConfirmation(
                customer.email(), bookingId, newSlot.startAt());

        log.info("Rescheduled booking {} from slot {} to slot {}",
                bookingId, oldSlot.slotId(), newSlot.slotId());

        return toResponse(bookingRepository.findById(bookingId), newSlot);
    }

    private BookingResponse toResponse(Booking booking, Slot slot) {
        return new BookingResponse(
                booking.bookingId(),
                booking.orderId(),
                booking.slotId(),
                booking.state().name(),
                slot.startAt());
    }
}
