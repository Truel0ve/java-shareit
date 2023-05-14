package ru.practicum.shareit.features.item.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.features.booking.model.BookingQueueInfo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    Long ownerId;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    BookingQueueInfo lastBooking;
    BookingQueueInfo nextBooking;
    List<CommentDto> comments = new ArrayList<>();
    Long requestId;
}