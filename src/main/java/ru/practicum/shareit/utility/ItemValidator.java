package ru.practicum.shareit.utility;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.exceptions.ValidationException;

@UtilityClass
public class ItemValidator {

    public void validateName(String name) throws ValidationException {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Item has empty name data");
        }
    }

    public void validateDescription(String description) throws ValidationException {
        if (description == null || description.isBlank()) {
            throw new ValidationException("Item has empty description data");
        }
        if (description.length() > 200) {
            throw new ValidationException("Description data of item exceeds more than 200 characters");
        }
    }

    public void validateAvailable(Boolean available) throws ValidationException {
        if (available == null) {
            throw new ValidationException("Item has empty available data");
        }
    }
}
