package ru.practicum.shareit.features.booking.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingQueueInfo {
    Long id;
    Long bookerId;
}