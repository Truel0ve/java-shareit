package ru.practicum.shareit.utility;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.features.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserValidatorTest {
    private final User user = new User();

    @Test
    void shouldNotValidateIfUserNameIsNull() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> UserValidator.validateName(user.getName()));
        assertEquals("User has empty name data",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotValidateIfUserNameIsBlank() {
        user.setName(" ");
        ValidationException exception = assertThrows(ValidationException.class,
                () -> UserValidator.validateName(user.getName()));
        assertEquals("User has empty name data",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotValidateIfUserEmailIsNull() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> UserValidator.validateEmail(user.getEmail()));
        assertEquals("User has wrong E-mail data",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotValidateIfUserEmailIsBlank() {
        user.setEmail(" ");
        ValidationException exception = assertThrows(ValidationException.class,
                () -> UserValidator.validateEmail(user.getEmail()));
        assertEquals("User has wrong E-mail data",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotValidateIfUserEmailHasWrongPattern() {
        user.setEmail("john_doeывадлшр@email.com");
        ValidationException exception = assertThrows(ValidationException.class,
                () -> UserValidator.validateEmail(user.getEmail()));
        assertEquals("User has wrong E-mail data",
                exception.getMessage(), "Invalid message");
    }
}
