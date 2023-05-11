package ru.practicum.shareit.features.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.features.booking.model.BookingDto;
import ru.practicum.shareit.features.booking.model.BookingShortDto;
import ru.practicum.shareit.features.booking.model.BookingStatus;
import ru.practicum.shareit.features.item.model.ItemShortDto;
import ru.practicum.shareit.features.user.model.UserShortDto;
import ru.practicum.shareit.utility.PageManager;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    MockMvc mvc;
    BookingShortDto bookingShort;
    BookingDto booking;

    @BeforeEach
    void setUp() {
        bookingShort = new BookingShortDto();
        bookingShort.setItemId(1L);
        bookingShort.setStart(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 12, 0, 10));
        bookingShort.setEnd(LocalDateTime.of(2023, Month.SEPTEMBER, 2, 12, 0, 10));

        booking = new BookingDto();
        booking.setId(1L);
        booking.setBooker(new UserShortDto(5L));
        booking.setItem(new ItemShortDto(10L, "Item"));
        booking.setStart(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 12, 0, 10));
        booking.setEnd(LocalDateTime.of(2023, Month.SEPTEMBER, 2, 12, 0, 10));
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    void shouldPostBooking() throws Exception {
        when(bookingService.create(anyLong(), any(BookingShortDto.class)))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingShort))
                        .header("X-Sharer-User-Id", 5)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));

        Mockito.verify(bookingService, Mockito.times(1))
                .create(5L, bookingShort);
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

    @Test
    void shouldPatchUser() throws Exception {
        when(bookingService.patch(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 10)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));

        Mockito.verify(bookingService, Mockito.times(1))
                .patch(10L, 1L, true);
    }

    @Test
    void shouldGetBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));

        Mockito.verify(bookingService, Mockito.times(1))
                .getBookingById(10L, 1L);
    }

    @ParameterizedTest
    @MethodSource("provideParamValues")
    void shouldGetBookingsOfBookerWithoutParams(String param, String value) throws Exception {
        when(bookingService.getBookingsOfBooker(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 5)
                        .param(param, value))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(booking.getStatus().toString())));

        Mockito.verify(bookingService, Mockito.times(1))
                .getBookingsOfBooker(5L, "ALL", Pageable.unpaged());
    }

    private static Stream<Arguments> provideParamValues() {
        return Stream.of(
                Arguments.of("from", "1"),
                Arguments.of("size", "1")
        );
    }

    @Test
    void shouldGetBookingsOfBookerWithParams() throws Exception {
        when(bookingService.getBookingsOfBooker(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 5)
                        .param("state", "PAST")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(booking.getStatus().toString())));

        Mockito.verify(bookingService, Mockito.times(1))
                .getBookingsOfBooker(5L, "PAST", PageManager.getPageable(1, 1));
    }

    @ParameterizedTest
    @MethodSource("provideParamValues")
    void shouldGetBookingsOfOwnerWithoutParams(String param, String value) throws Exception {
        when(bookingService.getBookingsOfOwner(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 10)
                        .param(param, value))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(booking.getStatus().toString())));

        Mockito.verify(bookingService, Mockito.times(1))
                .getBookingsOfOwner(10L, "ALL", Pageable.unpaged());
    }

    @Test
    void shouldGetBookingsOfOwnerWithParams() throws Exception {
        when(bookingService.getBookingsOfOwner(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 10)
                        .param("state", "PAST")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(booking.getStatus().toString())));

        Mockito.verify(bookingService, Mockito.times(1))
                .getBookingsOfOwner(10L, "PAST", PageManager.getPageable(1, 1));
    }
}