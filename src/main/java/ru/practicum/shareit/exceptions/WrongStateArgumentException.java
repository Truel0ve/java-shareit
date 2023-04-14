package ru.practicum.shareit.exceptions;

public class WrongStateArgumentException extends RuntimeException {

    public WrongStateArgumentException(final String message) {
        super(message);
    }
}
