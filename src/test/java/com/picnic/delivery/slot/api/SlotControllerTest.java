package com.picnic.delivery.slot.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.picnic.delivery.slot.api.dto.SlotResponse;
import com.picnic.delivery.slot.service.SlotService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SlotController.class)
@AutoConfigureMockMvc(addFilters = false)
class SlotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SlotService slotService;

    @Test
    void returnsAvailableSlots() throws Exception {
        SlotResponse slot = new SlotResponse(
                UUID.randomUUID(),
                "1012AB",
                OffsetDateTime.parse("2026-03-29T10:00:00Z"),
                OffsetDateTime.parse("2026-03-29T12:00:00Z"),
                20,
                4);

        when(slotService.findAvailableSlots(eq("1012AB"), any())).thenReturn(List.of(slot));

        mockMvc.perform(get("/slots?postcode=1012AB&date=2026-03-29"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].postcode").value("1012AB"))
                .andExpect(jsonPath("$[0].remainingCapacity").value(4));
    }
}
