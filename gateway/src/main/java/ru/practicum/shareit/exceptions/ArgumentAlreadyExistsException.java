package ru.practicum.shareit.exceptions;

public class ArgumentAlreadyExistsException extends RuntimeException {
    public ArgumentAlreadyExistsException(final String message) {
        super(message);
    }
}
