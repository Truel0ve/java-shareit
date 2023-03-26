package ru.practicum.shareit.features.booking.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    Long bookingId;
    Long itemId;
    Long bookerId;
    Timestamp start;
    Timestamp end;
    BookingStatus status;
}