package ru.practicum.shareit.features.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.features.booking.model.BookingShortDto;
import ru.practicum.shareit.features.booking.model.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getBookingsOfBooker(Long bookerId, String state, Pageable pageable);

    List<BookingDto> getBookingsOfOwner(Long ownerId, String state, Pageable pageable);

    BookingDto create(Long bookerId, BookingShortDto bookingDto);

    BookingDto patch(Long ownerId, Long bookingId, Boolean approved);
}