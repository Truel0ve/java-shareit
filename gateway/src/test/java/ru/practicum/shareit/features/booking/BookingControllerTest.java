package ru.practicum.shareit.features.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.features.booking.model.BookingShortDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@MockBean(classes = BookingClient.class)
@ContextConfiguration(classes = ShareItGateway.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    BookingShortDto bookingShort;

    @BeforeEach
    void setUp() {
        bookingShort = new BookingShortDto();
        bookingShort.setItemId(1L);
        bookingShort.setStart(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 12, 0, 10));
        bookingShort.setEnd(LocalDateTime.of(2023, Month.SEPTEMBER, 2, 12, 0, 10));
    }

    @Test
    void shouldNotPostBookingThenItemIdIsNull() throws Exception {
        bookingShort.setItemId(null);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingShort))
                        .header("X-Sharer-User-Id", 5)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.error", is("Invalid data format: itemId")));
    }

    @Test
    void shouldNotPostBookingThenStartIsNull() throws Exception {
        bookingShort.setStart(null);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingShort))
                        .header("X-Sharer-User-Id", 5)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.error", is("Invalid data format: start")));
    }

    @Test
    void shouldNotPostBookingThenEndIsNull() throws Exception {
        bookingShort.setEnd(null);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingShort))
                        .header("X-Sharer-User-Id", 5)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.error", is("Invalid data format: end")));
    }
}