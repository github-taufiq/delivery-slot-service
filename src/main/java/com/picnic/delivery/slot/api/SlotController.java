package com.picnic.delivery.slot.api;

import com.picnic.delivery.slot.api.dto.SlotResponse;
import com.picnic.delivery.slot.service.SlotService;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/slots")
public class SlotController {

    private static final Logger log = LoggerFactory.getLogger(SlotController.class);

    @Autowired
    private SlotService slotService;

    @GetMapping
    public List<SlotResponse> getSlots(
            @RequestParam("postcode") String postcode,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("Looking up slots for postcode {} on {}", postcode, date);

        return slotService.findAvailableSlots(postcode, date);
    }
}
