package ru.practicum.shareit.utility;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.ValidationException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PageManagerTest {

    @Test
    void shouldReturnPageableFromFirstPage() {
        Pageable pageable = PageManager.getPageable(1, 5);
        assertThat(pageable.getPageNumber(), equalTo(0));
        assertThat(pageable.getPageSize(), equalTo(5));
    }

    @Test
    void shouldReturnPageableFromOtherPage() {
        Pageable pageable = PageManager.getPageable(6, 3);
        assertThat(pageable.getPageNumber(), equalTo(2));
        assertThat(pageable.getPageSize(), equalTo(3));
    }

    @Test
    void shouldNotValidateIndex() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> PageManager.getPageable(-1, 1));
        assertEquals("Index of element must be at least 0",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotValidatePageSize() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> PageManager.getPageable(0, 0));
        assertEquals("Page size must be greater than 0",
                exception.getMessage(), "Invalid message");
    }
}