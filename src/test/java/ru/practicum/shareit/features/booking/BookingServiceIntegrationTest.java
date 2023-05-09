package ru.practicum.shareit.features.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @Test
    void shouldNotCreateBookingThenBookingStartDateBeforeNow() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(
                        booker.getId(),
                        setBookingShort(
                                item.getId(),
                                LocalDateTime.of(2022, Month.SEPTEMBER, 1, 12, 0, 0),
                                LocalDateTime.of(2023, Month.SEPTEMBER, 2, 12, 0, 0))));
        assertEquals("Wrong start data value",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotCreateBookingThenBookingEndDateBeforeNow() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(
                        booker.getId(),
                        setBookingShort(
                                item.getId(),
                                LocalDateTime.of(2023, Month.SEPTEMBER, 1, 12, 0, 0),
                                LocalDateTime.of(2022, Month.SEPTEMBER, 2, 12, 0, 0))));
        assertEquals("Wrong end data value",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotCreateBookingThenBookingEndDateBeforeStartDate() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(
                        booker.getId(),
                        setBookingShort(
                                item.getId(),
                                LocalDateTime.of(2024, Month.SEPTEMBER, 1, 12, 0, 0),
                                LocalDateTime.of(2023, Month.SEPTEMBER, 2, 12, 0, 0))));
        assertEquals("Wrong end data value",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotCreateBookingThenBookingStartDateAndEndDateTheSame() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(
                        booker.getId(),
                        setBookingShort(
                                item.getId(),
                                LocalDateTime.of(2023, Month.SEPTEMBER, 1, 12, 0, 0),
                                LocalDateTime.of(2023, Month.SEPTEMBER, 1, 12, 0, 0))));
        assertEquals("Start and end dates can`t be the same",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldPatchApprovedBooking() {
        BookingDto booking = bookingService.patch(owner.getId(), firstBooking.getId(), true);

        assertThat(booking.getId(), equalTo(firstBooking.getId()));
        assertThat(booking.getStart(), equalTo(firstBooking.getStart()));
        assertThat(booking.getEnd(), equalTo(firstBooking.getEnd()));
        assertThat(booking.getStatus(), equalTo(BookingStatus.APPROVED));
        assertThat(booking.getBooker().getId(), equalTo(firstBooking.getBooker().getId()));
        assertThat(booking.getItem().getId(), equalTo(firstBooking.getItem().getId()));
    }

    @Test
    void shouldPatchRejectedBooking() {
        BookingDto booking = bookingService.patch(owner.getId(), firstBooking.getId(), false);

        assertThat(booking.getId(), equalTo(firstBooking.getId()));
        assertThat(booking.getStart(), equalTo(firstBooking.getStart()));
        assertThat(booking.getEnd(), equalTo(firstBooking.getEnd()));
        assertThat(booking.getStatus(), equalTo(BookingStatus.REJECTED));
        assertThat(booking.getBooker().getId(), equalTo(firstBooking.getBooker().getId()));
        assertThat(booking.getItem().getId(), equalTo(firstBooking.getItem().getId()));
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

    @Test
    void shouldGetPastBookingsOfBooker() {
        Booking booking = entityManager.find(Booking.class, firstBooking.getId());
        booking.setStart(LocalDateTime.of(2022, Month.SEPTEMBER, 1, 12, 0, 0));
        booking.setEnd(LocalDateTime.of(2022, Month.SEPTEMBER, 2, 12, 0, 0));
        entityManager.merge(booking);

        List<BookingDto> bookings = bookingService.getBookingsOfBooker(booker.getId(), "PAST", Pageable.unpaged());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), equalTo(firstBooking.getId()));
    }

    @Test
    void shouldGetCurrentBookingsOfBooker() {
        Booking booking = entityManager.find(Booking.class, firstBooking.getId());
        booking.setStart(LocalDateTime.of(2022, Month.SEPTEMBER, 1, 12, 0, 0));
        booking.setEnd(LocalDateTime.of(2023, Month.SEPTEMBER, 2, 12, 0, 0));
        entityManager.merge(booking);

        List<BookingDto> bookings = bookingService.getBookingsOfBooker(booker.getId(), "CURRENT", Pageable.unpaged());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), equalTo(firstBooking.getId()));
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

    @Test
    void shouldGetPastBookingsOfrOwner() {
        Booking booking = entityManager.find(Booking.class, firstBooking.getId());
        booking.setStart(LocalDateTime.of(2022, Month.SEPTEMBER, 1, 12, 0, 0));
        booking.setEnd(LocalDateTime.of(2022, Month.SEPTEMBER, 2, 12, 0, 0));
        entityManager.merge(booking);

        List<BookingDto> bookings = bookingService.getBookingsOfOwner(owner.getId(), "PAST", Pageable.unpaged());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), equalTo(firstBooking.getId()));
    }

    @Test
    void shouldGetCurrentBookingsOfOwner() {
        Booking booking = entityManager.find(Booking.class, firstBooking.getId());
        booking.setStart(LocalDateTime.of(2022, Month.SEPTEMBER, 1, 12, 0, 0));
        booking.setEnd(LocalDateTime.of(2023, Month.SEPTEMBER, 2, 12, 0, 0));
        entityManager.merge(booking);

        List<BookingDto> bookings = bookingService.getBookingsOfOwner(owner.getId(), "CURRENT", Pageable.unpaged());

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