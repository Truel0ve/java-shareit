package ru.practicum.shareit.features.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.features.booking.model.Booking;
import ru.practicum.shareit.features.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Find ALL bookings of booker
    List<Booking> findAllByUserIdOrderByStartDesc(Long bookerId);

    // Find booking of booker for item
    List<Booking> findByUserIdAndItemIdOrderByStartAsc(Long bookerId, Long itemId);

    // Find all bookings for item
    List<Booking> findAllByItemIdOrderByStartAsc(Long itemId);

    // Find PAST bookings of booker
    List<Booking> findAllByUserIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime localDateTime);

    // Find CURRENT bookings of booker
    List<Booking> findAllByUserIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Long bookerId, LocalDateTime from, LocalDateTime to);

    // Find FUTURE bookings of booker
    List<Booking> findAllByUserIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime localDateTime);

    // Find bookings of booker with status
    List<Booking> findAllByUserIdAndStatusIsOrderByStartDesc(Long bookerId, BookingStatus bookingStatus);

    // Find ALL bookings of owner
    List<Booking> findAllByItemUserIdOrderByStartDesc(Long ownerId);

    // Find PAST bookings of owner
    List<Booking> findAllByItemUserIdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime localDateTime);

    // Find CURRENT bookings of owner
    List<Booking> findAllByItemUserIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Long ownerId, LocalDateTime from, LocalDateTime to);

    // Find FUTURE bookings of owner
    List<Booking> findAllByItemUserIdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime localDateTime);

    // Find bookings of owner with status
    List<Booking> findAllByItemUserIdAndStatusIsOrderByStartDesc(Long ownerId, BookingStatus bookingStatus);

    // Patch booking status
    @Modifying
    @Query("UPDATE Booking b " +
            "SET b.status = ?2 " +
            "WHERE b.id = ?1")
    void patch(Long bookingId, BookingStatus bookingStatus);
}