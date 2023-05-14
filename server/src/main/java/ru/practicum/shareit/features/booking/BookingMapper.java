package ru.practicum.shareit.features.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.features.booking.model.Booking;
import ru.practicum.shareit.features.booking.model.BookingQueueInfo;
import ru.practicum.shareit.features.booking.model.BookingShortDto;
import ru.practicum.shareit.features.booking.model.BookingDto;

@UtilityClass
public class BookingMapper {

    public Booking toBooking(BookingShortDto bookingShortDto) {
        Booking booking = new Booking();
        booking.setStart(bookingShortDto.getStart());
        booking.setEnd(bookingShortDto.getEnd());
        return booking;
    }

    public BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public BookingQueueInfo toBookingQueue(Booking booking) {
        BookingQueueInfo bQueueInfo = new BookingQueueInfo();
        bQueueInfo.setId(booking.getId());
        bQueueInfo.setBookerId(booking.getUser().getId());
        return bQueueInfo;
    }
}
