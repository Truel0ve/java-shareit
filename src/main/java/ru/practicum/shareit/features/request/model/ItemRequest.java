package ru.practicum.shareit.features.request.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ItemRequest {
    private Long requestId;
    private Long requestorId;
    private String description;
    private Timestamp creationDate;
}