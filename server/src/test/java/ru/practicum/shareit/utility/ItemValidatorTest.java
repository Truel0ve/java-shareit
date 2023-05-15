package ru.practicum.shareit.utility;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.features.item.model.ItemDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemValidatorTest {
    private final ItemDto item = new ItemDto();

    @Test
    void shouldNotValidateIfItemNameIsNull() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemValidator.validateName(item.getName()));
        assertEquals("Item has empty name data",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotValidateIfItemNameIsBlank() {
        item.setName(" ");
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemValidator.validateName(item.getName()));
        assertEquals("Item has empty name data",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotValidateIfItemDescriptionIsNull() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemValidator.validateDescription(item.getDescription()));
        assertEquals("Item has empty description data",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotValidateIfItemDescriptionIsBlank() {
        item.setDescription(" ");
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemValidator.validateDescription(item.getDescription()));
        assertEquals("Item has empty description data",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotValidateIfItemDescriptionIsMoreThen200Chars() {
        item.setDescription("Weber is a well-known brand in the world of grilling, and their product descriptions do " +
                "a great job of highlighting the features of their products while also explaining how those features " +
                "benefit the customer. In this product description, Weber opens with, “Open the door to the world of " +
                "grilling with crazy good food and friends coming together. This is a great way to start because it " +
                "immediately sets the tone for what the customer would experience with this grill in their life. It’s " +
                "not just a grill, it’s a way to entertain and spend time with friends.");
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemValidator.validateDescription(item.getDescription()));
        assertEquals("Description data of item exceeds more than 200 characters",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotValidateIfItemAvailableIsNull() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemValidator.validateAvailable(item.getAvailable()));
        assertEquals("Item has empty available data",
                exception.getMessage(), "Invalid message");
    }
}