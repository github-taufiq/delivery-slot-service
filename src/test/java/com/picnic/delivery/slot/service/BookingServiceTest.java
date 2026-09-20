package com.picnic.delivery.slot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID SLOT_ID = UUID.randomUUID();
    private static final UUID NEW_SLOT_ID = UUID.randomUUID();
    private static final UUID WAREHOUSE_ID = UUID.randomUUID();
    private static final UUID BOOKING_ID = UUID.randomUUID();

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private SlotRepository slotRepository;

    @Mock
    private OrderClient orderClient;

    @Mock
    private CustomerClient customerClient;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createsBookingWhenSlotHasCapacity() {
        Slot slot = slot(SLOT_ID, Duration.ofHours(6), 5);

        when(orderClient.getOrder(ORDER_ID)).thenReturn(new Order(ORDER_ID, CUSTOMER_ID, "1012AB"));
        when(slotRepository.findById(SLOT_ID)).thenReturn(slot);
        when(bookingRepository.findByOrderId(ORDER_ID)).thenReturn(List.of());
        when(bookingRepository.insert(any(), any(), any(), any())).thenReturn(booking(SLOT_ID));
        when(customerClient.getCustomer(CUSTOMER_ID))
                .thenReturn(new Customer(CUSTOMER_ID, "customer@example.com"));

        BookingResponse response =
                bookingService.createBooking(new CreateBookingRequest(ORDER_ID, CUSTOMER_ID, SLOT_ID));

        assertThat(response.state()).isEqualTo("ACTIVE");
        verify(slotRepository).updateRemainingCapacity(SLOT_ID, 4);
    }

    @Test
    void rejectsBookingWhenSlotIsFull() {
        when(orderClient.getOrder(ORDER_ID)).thenReturn(new Order(ORDER_ID, CUSTOMER_ID, "1012AB"));
        when(slotRepository.findById(SLOT_ID)).thenReturn(slot(SLOT_ID, Duration.ofHours(6), 0));

        assertThatThrownBy(() ->
                bookingService.createBooking(new CreateBookingRequest(ORDER_ID, CUSTOMER_ID, SLOT_ID)))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void reschedulesBookingToAnotherSlot() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(booking(SLOT_ID));
        when(slotRepository.findById(NEW_SLOT_ID)).thenReturn(slot(NEW_SLOT_ID, Duration.ofHours(8), 3));
        when(slotRepository.findById(SLOT_ID)).thenReturn(slot(SLOT_ID, Duration.ofHours(6), 2));
        when(customerClient.getCustomer(CUSTOMER_ID))
                .thenReturn(new Customer(CUSTOMER_ID, "customer@example.com"));

        bookingService.reschedule(BOOKING_ID, new RescheduleBookingRequest(NEW_SLOT_ID));

        verify(slotRepository).updateRemainingCapacity(SLOT_ID, 3);
        verify(slotRepository).updateRemainingCapacity(NEW_SLOT_ID, 2);
        verify(bookingRepository).updateSlot(BOOKING_ID, NEW_SLOT_ID);
    }

    private Slot slot(UUID slotId, Duration startsIn, int remainingCapacity) {
        OffsetDateTime start = OffsetDateTime.now().plus(startsIn);
        return new Slot(slotId, "1012AB", WAREHOUSE_ID, start, start.plusHours(2), 20, remainingCapacity);
    }

    private Booking booking(UUID slotId) {
        return new Booking(
                BOOKING_ID, ORDER_ID, CUSTOMER_ID, slotId, BookingState.ACTIVE, OffsetDateTime.now());
    }
}
