package ru.practicum.shareit.features.booking.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingShortDto {
    @NotNull
    Long itemId;
    @NotNull
    LocalDateTime start;
    @NotNull
    LocalDateTime end;
}