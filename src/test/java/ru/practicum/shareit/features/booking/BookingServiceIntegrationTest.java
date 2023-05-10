package ru.practicum.shareit.features.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ArgumentNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongStateArgumentException;
import ru.practicum.shareit.features.booking.model.*;
import ru.practicum.shareit.features.item.model.Item;
import ru.practicum.shareit.features.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingServiceIntegrationTest {
    final EntityManager entityManager;
    final BookingService bookingService;
    User owner;
    User booker;
    Item item;
    BookingDto firstBooking;
    BookingDto secondBooking;

    @BeforeEach
    void setUp() {
        owner = setUser("John Doe", "john_doe@email.com");
        booker = setUser("Jane Doe", "jane_doe@email.com");

        item = new Item();
        item.setUser(owner);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        entityManager.persist(item);
        entityManager.flush();

        firstBooking = bookingService.create(
                booker.getId(),
                setBookingShort(
                        item.getId(),
                        LocalDateTime.of(2023, Month.SEPTEMBER, 1, 12, 0, 0),
                        LocalDateTime.of(2023, Month.SEPTEMBER, 2, 12, 0, 0)));

        secondBooking = bookingService.create(
                booker.getId(),
                setBookingShort(
                        item.getId(),
                        LocalDateTime.of(2023, Month.OCTOBER, 1, 12, 0, 0),
                        LocalDateTime.of(2023, Month.OCTOBER, 2, 12, 0, 0)));
    }

    @Test
    void shouldCreateBooking() {
        assertThat(firstBooking.getId(), notNullValue());
        assertThat(firstBooking.getStart(), equalTo(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 12, 0, 0)));
        assertThat(firstBooking.getEnd(), equalTo(LocalDateTime.of(2023, Month.SEPTEMBER, 2, 12, 0, 0)));
        assertThat(firstBooking.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(firstBooking.getBooker().getId(), equalTo(booker.getId()));
        assertThat(firstBooking.getItem().getId(), equalTo(item.getId()));
    }

    @Test
    void shouldNotCreateBookingByOwner() {
        ArgumentNotFoundException exception = assertThrows(ArgumentNotFoundException.class,
                () -> bookingService.create(
                        owner.getId(),
                        setBookingShort(
                                item.getId(),
                                LocalDateTime.of(2023, Month.SEPTEMBER, 1, 12, 0, 0),
                                LocalDateTime.of(2023, Month.SEPTEMBER, 2, 12, 0, 0))));
        assertEquals("Unable to create booking. The specified user id=" + owner.getId() +
                        " is the owner of item id=" + item.getId(),
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotCreateBookingThenAvailableIsFalse() {
        item.setAvailable(false);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(
                        booker.getId(),
                        setBookingShort(
                                item.getId(),
                                LocalDateTime.of(2023, Month.SEPTEMBER, 1, 12, 0, 0),
                                LocalDateTime.of(2023, Month.SEPTEMBER, 2, 12, 0, 0))));
        assertEquals("The specified item id=" + item.getId() + " is not available",
                exception.getMessage(), "Invalid message");
    }

    @ParameterizedTest
    @MethodSource("provideDatesForValidationException")
    void shouldNotCreateBookingThenBookingDatesIsWrong(LocalDateTime start, LocalDateTime end, String message) {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(booker.getId(), setBookingShort(item.getId(), start, end)));
        assertEquals(message, exception.getMessage(), "Invalid message");
    }

    private static Stream<Arguments> provideDatesForValidationException() {
        return Stream.of(
                Arguments.of(LocalDateTime.of(2022, Month.SEPTEMBER, 1, 12, 0, 0),
                        LocalDateTime.of(2023, Month.SEPTEMBER, 2, 12, 0, 0),
                        "Wrong start data value"),
                Arguments.of(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 12, 0, 0),
                        LocalDateTime.of(2022, Month.SEPTEMBER, 2, 12, 0, 0),
                        "Wrong end data value"),
                Arguments.of(LocalDateTime.of(2024, Month.SEPTEMBER, 1, 12, 0, 0),
                        LocalDateTime.of(2023, Month.SEPTEMBER, 2, 12, 0, 0),
                        "Wrong end data value"),
                Arguments.of(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 12, 0, 0),
                        LocalDateTime.of(2023, Month.SEPTEMBER, 1, 12, 0, 0),
                        "Start and end dates can`t be the same")
        );
    }

    @ParameterizedTest
    @MethodSource("provideValuesForPatchBookings")
    void shouldPatchApprovedBooking(Boolean input, BookingStatus status) {
        BookingDto booking = bookingService.patch(owner.getId(), firstBooking.getId(), input);

        assertThat(booking.getId(), equalTo(firstBooking.getId()));
        assertThat(booking.getStart(), equalTo(firstBooking.getStart()));
        assertThat(booking.getEnd(), equalTo(firstBooking.getEnd()));
        assertThat(booking.getStatus(), equalTo(status));
        assertThat(booking.getBooker().getId(), equalTo(firstBooking.getBooker().getId()));
        assertThat(booking.getItem().getId(), equalTo(firstBooking.getItem().getId()));
    }

    private static Stream<Arguments> provideValuesForPatchBookings() {
        return Stream.of(
                Arguments.of(true, BookingStatus.APPROVED),
                Arguments.of(false, BookingStatus.REJECTED)
        );
    }

    @Test
    void shouldNotPatchThenBookingStatusNotWaiting() {
        Booking booking = entityManager.find(Booking.class, firstBooking.getId());
        booking.setStatus(BookingStatus.APPROVED);
        entityManager.merge(booking);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.patch(owner.getId(), firstBooking.getId(), true));
        assertEquals("Booking is already " + BookingStatus.APPROVED + " by owner of item",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotPatchBookingByBooker() {
        ArgumentNotFoundException exception = assertThrows(ArgumentNotFoundException.class,
                () -> bookingService.patch(booker.getId(), firstBooking.getId(), true));
        assertEquals("Unable to approve booking. The specified user id=" + booker.getId() +
                        " is not the owner of item id=" + firstBooking.getItem().getId(),
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldGetBookingByIdForOwner() {
        BookingDto booking = bookingService.getBookingById(owner.getId(), firstBooking.getId());

        assertThat(booking.getId(), equalTo(firstBooking.getId()));
        assertThat(booking.getStart(), equalTo(firstBooking.getStart()));
        assertThat(booking.getEnd(), equalTo(firstBooking.getEnd()));
        assertThat(booking.getStatus(), equalTo(firstBooking.getStatus()));
        assertThat(booking.getBooker().getId(), equalTo(firstBooking.getBooker().getId()));
        assertThat(booking.getItem().getId(), equalTo(firstBooking.getItem().getId()));
    }

    @Test
    void shouldGetBookingByIdForBooker() {
        BookingDto booking = bookingService.getBookingById(booker.getId(), firstBooking.getId());

        assertThat(booking.getId(), equalTo(firstBooking.getId()));
        assertThat(booking.getStart(), equalTo(firstBooking.getStart()));
        assertThat(booking.getEnd(), equalTo(firstBooking.getEnd()));
        assertThat(booking.getStatus(), equalTo(firstBooking.getStatus()));
        assertThat(booking.getBooker().getId(), equalTo(firstBooking.getBooker().getId()));
        assertThat(booking.getItem().getId(), equalTo(firstBooking.getItem().getId()));
    }

    @Test
    void shouldNotGetBookingByWrongUserId() {
        ArgumentNotFoundException exception = assertThrows(ArgumentNotFoundException.class,
                () -> bookingService.getBookingById(99L, firstBooking.getId()));
        assertEquals("User id=" + 99 + " is not the booker/owner of item id=" +
                        firstBooking.getItem().getId() + " or the specified item does not exist",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotGetBookingByWrongBookingId() {
        ArgumentNotFoundException exception = assertThrows(ArgumentNotFoundException.class,
                () -> bookingService.getBookingById(owner.getId(), 99L));
        assertEquals("The specified booking id=" + 99 + " does not exist",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldGetAllBookingsOfBooker() {
        List<BookingDto> bookings = bookingService.getBookingsOfBooker(booker.getId(), "ALL", Pageable.unpaged());

        assertThat(bookings, hasSize(2));
        assertThat(bookings.get(0).getId(), equalTo(secondBooking.getId()));
        assertThat(bookings.get(1).getId(), equalTo(firstBooking.getId()));
    }

    @ParameterizedTest
    @MethodSource("provideValuesForGetPastAndCurrentBookings")
    void shouldGetPastAndCurrentBookingsOfBooker(LocalDateTime start, LocalDateTime end, String state) {
        Booking booking = entityManager.find(Booking.class, firstBooking.getId());
        booking.setStart(start);
        booking.setEnd(end);
        entityManager.merge(booking);

        List<BookingDto> bookings = bookingService.getBookingsOfBooker(booker.getId(), state, Pageable.unpaged());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), equalTo(firstBooking.getId()));
    }

    private static Stream<Arguments> provideValuesForGetPastAndCurrentBookings() {
        return Stream.of(
                Arguments.of(LocalDateTime.of(2022, Month.SEPTEMBER, 1, 12, 0, 0),
                        LocalDateTime.of(2022, Month.SEPTEMBER, 2, 12, 0, 0),
                        "PAST"),
                Arguments.of(LocalDateTime.of(2022, Month.SEPTEMBER, 1, 12, 0, 0),
                        LocalDateTime.of(2023, Month.SEPTEMBER, 2, 12, 0, 0),
                        "CURRENT")
        );
    }

    @Test
    void shouldGetFutureBookingsOfBooker() {
        List<BookingDto> bookings = bookingService.getBookingsOfBooker(booker.getId(), "FUTURE", Pageable.unpaged());

        assertThat(bookings, hasSize(2));
        assertThat(bookings.get(0).getId(), equalTo(secondBooking.getId()));
        assertThat(bookings.get(1).getId(), equalTo(firstBooking.getId()));
    }

    @Test
    void shouldGetWaitingBookingsOfBooker() {
        Booking booking = entityManager.find(Booking.class, firstBooking.getId());
        booking.setStatus(BookingStatus.APPROVED);
        entityManager.merge(booking);

        List<BookingDto> bookings = bookingService.getBookingsOfBooker(booker.getId(), "WAITING", Pageable.unpaged());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), equalTo(secondBooking.getId()));
    }

    @Test
    void shouldGetRejectedBookingsOfBooker() {
        Booking booking = entityManager.find(Booking.class, firstBooking.getId());
        booking.setStatus(BookingStatus.REJECTED);
        entityManager.merge(booking);

        List<BookingDto> bookings = bookingService.getBookingsOfBooker(booker.getId(), "REJECTED", Pageable.unpaged());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), equalTo(firstBooking.getId()));
    }

    @Test
    void shouldNotGetBookingsOfBookerWithWrongState() {
        WrongStateArgumentException exception = assertThrows(WrongStateArgumentException.class,
                () -> bookingService.getBookingsOfBooker(booker.getId(), "WRONG STATE", Pageable.unpaged()));
        assertEquals("Unknown state: WRONG STATE",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldGetAllBookingsOfOwner() {
        List<BookingDto> bookings = bookingService.getBookingsOfOwner(owner.getId(), "ALL", Pageable.unpaged());

        assertThat(bookings, hasSize(2));
        assertThat(bookings.get(0).getId(), equalTo(secondBooking.getId()));
        assertThat(bookings.get(1).getId(), equalTo(firstBooking.getId()));
    }

    @ParameterizedTest
    @MethodSource("provideValuesForGetPastAndCurrentBookings")
    void shouldGetPastAndCurrentBookingsOfOwner(LocalDateTime start, LocalDateTime end, String state) {
        Booking booking = entityManager.find(Booking.class, firstBooking.getId());
        booking.setStart(start);
        booking.setEnd(end);
        entityManager.merge(booking);

        List<BookingDto> bookings = bookingService.getBookingsOfOwner(owner.getId(), state, Pageable.unpaged());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), equalTo(firstBooking.getId()));
    }

    @Test
    void shouldGetFutureBookingsOfOwner() {
        List<BookingDto> bookings = bookingService.getBookingsOfOwner(owner.getId(), "FUTURE", Pageable.unpaged());

        assertThat(bookings, hasSize(2));
        assertThat(bookings.get(0).getId(), equalTo(secondBooking.getId()));
        assertThat(bookings.get(1).getId(), equalTo(firstBooking.getId()));
    }

    @Test
    void shouldGetWaitingBookingsOfOwner() {
        Booking booking = entityManager.find(Booking.class, firstBooking.getId());
        booking.setStatus(BookingStatus.APPROVED);
        entityManager.merge(booking);

        List<BookingDto> bookings = bookingService.getBookingsOfOwner(owner.getId(), "WAITING", Pageable.unpaged());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), equalTo(secondBooking.getId()));
    }

    @Test
    void shouldGetRejectedBookingsOfOwner() {
        Booking booking = entityManager.find(Booking.class, firstBooking.getId());
        booking.setStatus(BookingStatus.REJECTED);
        entityManager.merge(booking);

        List<BookingDto> bookings = bookingService.getBookingsOfOwner(owner.getId(), "REJECTED", Pageable.unpaged());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), equalTo(firstBooking.getId()));
    }

    @Test
    void shouldNotGetBookingsOfOwnerWithWrongState() {
        WrongStateArgumentException exception = assertThrows(WrongStateArgumentException.class,
                () -> bookingService.getBookingsOfOwner(owner.getId(), "WRONG STATE", Pageable.unpaged()));
        assertEquals("Unknown state: WRONG STATE",
                exception.getMessage(), "Invalid message");
    }

    private User setUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    private BookingShortDto setBookingShort(Long itemId, LocalDateTime start, LocalDateTime end) {
        BookingShortDto booking = new BookingShortDto();
        booking.setItemId(itemId);
        booking.setStart(start);
        booking.setEnd(end);
        return booking;
    }
}