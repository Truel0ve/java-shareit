package ru.practicum.shareit.utility;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.ValidationException;

@UtilityClass
public class PageManager {

    public Pageable getPageable(Integer from, Integer size) {
        validateElementIndex(from);
        validatePageSize(size);
        if (from <= size - 1) {
            return PageRequest.of(0, size);
        } else {
            return PageRequest.of(from / size, size);
        }
    }

    private void validateElementIndex(Integer index) {
        if (index < 0) {
            throw new ValidationException("Index of element must be at least 0");
        }
    }

    private void validatePageSize(Integer page) {
        if (page < 1) {
            throw new ValidationException("Page size must be greater than 0");
        }
    }
}