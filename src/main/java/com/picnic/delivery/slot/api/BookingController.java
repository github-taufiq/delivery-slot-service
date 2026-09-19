package com.picnic.delivery.slot.api;

import com.picnic.delivery.slot.api.dto.BookingResponse;
import com.picnic.delivery.slot.api.dto.CreateBookingRequest;
import com.picnic.delivery.slot.api.dto.RescheduleBookingRequest;
import com.picnic.delivery.slot.service.BookingService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public BookingResponse create(@RequestBody @Valid CreateBookingRequest request) {
        return bookingService.createBooking(request);
    }

    @PutMapping("/{id}")
    public BookingResponse reschedule(
            @PathVariable("id") UUID id,
            @RequestBody @Valid RescheduleBookingRequest request,
            Authentication authentication) {

        return bookingService.reschedule(id, request);
    }
}
