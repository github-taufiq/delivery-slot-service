package com.picnic.delivery.slot.repository;

import static com.picnic.delivery.jooq.Tables.BOOKINGS;

import com.picnic.delivery.slot.domain.Booking;
import com.picnic.delivery.slot.domain.BookingState;
import java.util.List;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

@Repository
public class BookingRepository {

    private final DSLContext dsl;

    public BookingRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Booking insert(UUID orderId, UUID customerId, UUID slotId, BookingState state) {
        return dsl.insertInto(BOOKINGS)
                .set(BOOKINGS.ORDER_ID, orderId)
                .set(BOOKINGS.CUSTOMER_ID, customerId)
                .set(BOOKINGS.SLOT_ID, slotId)
                .set(BOOKINGS.STATE, state.name())
                .returning()
                .fetchOne()
                .map(this::toBooking);
    }

    public Booking findById(UUID bookingId) {
        return dsl.selectFrom(BOOKINGS)
                .where(BOOKINGS.BOOKING_ID.eq(bookingId))
                .fetchOne(this::toBooking);
    }

    public List<Booking> findByOrderId(UUID orderId) {
        return dsl.selectFrom(BOOKINGS)
                .where(BOOKINGS.ORDER_ID.eq(orderId))
                .fetch(this::toBooking);
    }

    public void updateSlot(UUID bookingId, UUID slotId) {
        dsl.update(BOOKINGS)
                .set(BOOKINGS.SLOT_ID, slotId)
                .where(BOOKINGS.BOOKING_ID.eq(bookingId))
                .execute();
    }

    public void updateState(UUID bookingId, BookingState state) {
        dsl.update(BOOKINGS)
                .set(BOOKINGS.STATE, state.name())
                .where(BOOKINGS.BOOKING_ID.eq(bookingId))
                .execute();
    }

    private Booking toBooking(Record record) {
        return new Booking(
                record.get(BOOKINGS.BOOKING_ID),
                record.get(BOOKINGS.ORDER_ID),
                record.get(BOOKINGS.CUSTOMER_ID),
                record.get(BOOKINGS.SLOT_ID),
                BookingState.valueOf(record.get(BOOKINGS.STATE)),
                record.get(BOOKINGS.CREATED_AT));
    }
}
