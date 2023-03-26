package ru.practicum.shareit.features.request.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@FieldDefaults(level= AccessLevel.PRIVATE)
public class ItemRequestDto {
    Long requestId;
    Long requestorId;
    String description;
    Timestamp creationDate;
}