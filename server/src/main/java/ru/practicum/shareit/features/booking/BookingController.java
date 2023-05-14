package ru.practicum.shareit.features.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.features.booking.model.BookingShortDto;
import ru.practicum.shareit.features.booking.model.BookingDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(USER_ID_HEADER) Long userId,
                                     @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsOfBooker(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                                @RequestParam(defaultValue = "ALL", required = false) String state,
                                                @RequestParam(defaultValue = "0", required = false) Integer from,
                                                @RequestParam(defaultValue = "10", required = false) Integer size) {
        return bookingService.getBookingsOfBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOfOwner(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                               @RequestParam(defaultValue = "ALL", required = false) String state,
                                               @RequestParam(defaultValue = "0", required = false) Integer from,
                                               @RequestParam(defaultValue = "10", required = false) Integer size) {
        return bookingService.getBookingsOfOwner(ownerId, state, from, size);
    }

    @PostMapping
    public BookingDto postBooking(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                  @RequestBody BookingShortDto bookingShortDto) {
        return bookingService.create(bookerId, bookingShortDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchBooking(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                   @PathVariable Long bookingId,
                                   @RequestParam Boolean approved) {
        return bookingService.patch(ownerId, bookingId, approved);
    }
}