package ru.practicum.shareit.features.booking.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.features.item.model.ItemShortDto;
import ru.practicum.shareit.features.user.model.UserShortDto;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    BookingStatus status;
    UserShortDto booker;
    ItemShortDto item;
}