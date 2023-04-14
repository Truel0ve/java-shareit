package ru.practicum.shareit.features.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.features.booking.model.BookingShortDto;
import ru.practicum.shareit.features.booking.model.BookingDto;
import ru.practicum.shareit.utility.RequestLogger;

import javax.validation.Valid;
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
        RequestLogger.logRequest(RequestMethod.GET, "/bookings/" + bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsOfBooker(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                                @RequestParam(defaultValue = "ALL", required = false) String state) {
        RequestLogger.logRequest(RequestMethod.GET, "/bookings?state=" + state);
        return bookingService.getBookingsOfBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOfOwner(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                               @RequestParam(defaultValue = "ALL", required = false) String state) {
        RequestLogger.logRequest(RequestMethod.GET, "/bookings/owner?state=" + state);
        return bookingService.getBookingsOfOwner(ownerId, state);
    }

    @PostMapping
    public BookingDto postBooking(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                  @Valid @RequestBody BookingShortDto bookingShortDto) {
        RequestLogger.logRequest(RequestMethod.POST, "/bookings");
        return bookingService.create(bookerId, bookingShortDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchBooking(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                   @PathVariable Long bookingId,
                                   @RequestParam Boolean approved) {
        RequestLogger.logRequest(RequestMethod.PATCH, "/bookings/" + bookingId + "?approved=" + approved);
        return bookingService.patch(ownerId, bookingId, approved);
    }
}