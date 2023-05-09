package ru.practicum.shareit.features.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.features.booking.model.Booking;
import ru.practicum.shareit.features.booking.model.BookingStatus;
import ru.practicum.shareit.features.item.model.Item;
import ru.practicum.shareit.features.user.model.User;

import java.time.LocalDateTime;
import java.time.Month;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    BookingRepository bookingRepository;
    Booking booking;

    @BeforeEach
    void setUp() {
        User booker = setUser("John Doe", "john_doe@email.com");
        User owner = setUser("Jane Doe", "jane_doe@email.com");

        Item item = new Item();
        item.setUser(owner);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        entityManager.persist(item);
        entityManager.flush();

        booking = new Booking();
        booking.setItem(item);
        booking.setUser(booker);
        booking.setStart(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 12, 0, 10));
        booking.setEnd(LocalDateTime.of(2023, Month.SEPTEMBER, 2, 12, 0, 10));
        booking.setStatus(BookingStatus.WAITING);
        entityManager.persist(booking);
        entityManager.flush();
    }

    @Test
    void shouldPatchBooking() {
        bookingRepository.patch(booking.getId(), BookingStatus.APPROVED);
        Booking patchedBooking = entityManager.find(Booking.class, booking.getId());

        assertThat(patchedBooking.getId(), equalTo(booking.getId()));
        assertThat(patchedBooking.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(patchedBooking.getUser().getId(), equalTo(booking.getUser().getId()));
        assertThat(patchedBooking.getStart(), equalTo(booking.getStart()));
        assertThat(patchedBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(patchedBooking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    private User setUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }
}