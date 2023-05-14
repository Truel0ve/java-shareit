package ru.practicum.shareit.features.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.features.booking.model.BookingShortDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable Long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsOfBooker(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                                @RequestParam(defaultValue = "ALL", required = false) String state,
                                                @RequestParam(defaultValue = "0", required = false) Integer from,
                                                @RequestParam(defaultValue = "10", required = false) Integer size) {
        return bookingClient.getBookingsOfBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOfOwner(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                               @RequestParam(defaultValue = "ALL", required = false) String state,
                                               @RequestParam(defaultValue = "0", required = false) Integer from,
                                               @RequestParam(defaultValue = "10", required = false) Integer size) {
        return bookingClient.getBookingsOfOwner(ownerId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> postBooking(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                  @Valid @RequestBody BookingShortDto bookingShortDto) {
        return bookingClient.postBooking(bookerId, bookingShortDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patchBooking(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                   @PathVariable Long bookingId,
                                   @RequestParam Boolean approved) {
        return bookingClient.patchBooking(ownerId, bookingId, approved);
    }
}