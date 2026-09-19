package com.picnic.delivery.slot.service;

import com.picnic.delivery.slot.api.dto.SlotResponse;
import com.picnic.delivery.slot.domain.Slot;
import com.picnic.delivery.slot.repository.SlotRepository;
import com.picnic.routing.client.RouteClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SlotService {

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private RouteClient routeClient;

    public List<SlotResponse> findAvailableSlots(String postcode, LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

        List<Slot> slots = slotRepository.findAvailable(postcode, dayStart, dayEnd);

        return slots.stream()
                .filter(slot -> routeClient.hasRouteCapacity(slot.warehouseId(), slot.slotId()))
                .map(slot -> new SlotResponse(
                        slot.slotId(),
                        slot.postcode(),
                        slot.startAt(),
                        slot.endAt(),
                        slot.totalCapacity(),
                        slot.remainingCapacity()))
                .toList();
    }
}
