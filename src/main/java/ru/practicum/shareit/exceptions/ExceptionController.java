package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice(value = "ru.practicum.shareit")
@Slf4j
public class ExceptionController {

    @ExceptionHandler({ValidationException.class, BindException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final Exception e) {
        String message;
        String defaultMessage = "Invalid data format: ";
        if (e instanceof BindException) {
            message = defaultMessage + Objects.requireNonNull(((BindException) e).getFieldError()).getField();
        } else if (e.getClass().equals(HttpMessageNotReadableException.class)) {
            message = defaultMessage + ((HttpMessageNotReadableException) e).getMostSpecificCause();
        } else {
            message = e.getMessage();
        }
        log.warn(message, e);
        return new ErrorResponse(message);
    }

    @ExceptionHandler({NullPointerException.class, ArgumentNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final Exception e) {
        log.warn(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ArgumentAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistsException(final Exception e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.warn(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }
}