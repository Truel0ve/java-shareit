package ru.practicum.shareit.features.booking.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BookingDto {
    private Long bookingId;
    private Long itemId;
    private Long bookerId;
    private Timestamp start;
    private Timestamp end;
    private BookingStatus status;
}
