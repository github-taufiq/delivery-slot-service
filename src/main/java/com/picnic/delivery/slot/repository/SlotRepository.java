package com.picnic.delivery.slot.repository;

import static com.picnic.delivery.jooq.Tables.SLOTS;

import com.picnic.delivery.slot.domain.Slot;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

@Repository
public class SlotRepository {

    private final DSLContext dsl;

    public SlotRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<Slot> findAvailable(String postcode, LocalDateTime from, LocalDateTime to) {
        return dsl.selectFrom(SLOTS)
                .where(SLOTS.POSTCODE.eq(postcode))
                .and(SLOTS.START_AT.ge(from.atZone(ZoneId.systemDefault()).toOffsetDateTime()))
                .and(SLOTS.START_AT.lt(to.atZone(ZoneId.systemDefault()).toOffsetDateTime()))
                .and(SLOTS.REMAINING_CAPACITY.gt(0))
                .orderBy(SLOTS.START_AT.asc())
                .fetch(this::toSlot);
    }

    public Slot findById(UUID slotId) {
        return dsl.selectFrom(SLOTS)
                .where(SLOTS.SLOT_ID.eq(slotId))
                .fetchOne(this::toSlot);
    }

    public void updateRemainingCapacity(UUID slotId, int remainingCapacity) {
        dsl.update(SLOTS)
                .set(SLOTS.REMAINING_CAPACITY, remainingCapacity)
                .where(SLOTS.SLOT_ID.eq(slotId))
                .execute();
    }

    private Slot toSlot(Record record) {
        return new Slot(
                record.get(SLOTS.SLOT_ID),
                record.get(SLOTS.POSTCODE),
                record.get(SLOTS.WAREHOUSE_ID),
                record.get(SLOTS.START_AT),
                record.get(SLOTS.END_AT),
                record.get(SLOTS.TOTAL_CAPACITY),
                record.get(SLOTS.REMAINING_CAPACITY));
    }
}
