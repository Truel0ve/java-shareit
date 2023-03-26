package ru.practicum.shareit.utility;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.regex.Pattern;

@UtilityClass
public class UserValidator {

    public void validateName(String name) throws ValidationException {
        if (name == null || name.isBlank()) {
            throw new ValidationException("User has empty name data");
        }
    }

    public void validateEmail(String email) throws ValidationException {
        if (!Pattern.matches("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", email)) {
            throw new ValidationException("User has wrong E-mail data");
        }
    }
}