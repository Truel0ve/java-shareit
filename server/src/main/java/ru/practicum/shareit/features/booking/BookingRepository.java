package ru.practicum.shareit.features.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.features.booking.model.Booking;
import ru.practicum.shareit.features.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Find ALL bookings of booker
    Page<Booking> findAllByUserIdOrderByStartDesc(Long bookerId, Pageable pageable);

    // Find booking of booker for item
    List<Booking> findByUserIdAndItemIdOrderByStartAsc(Long bookerId, Long itemId);

    // Find all bookings for item
    List<Booking> findAllByItemIdOrderByStartAsc(Long itemId);

    // Find PAST bookings of booker
    Page<Booking> findAllByUserIdAndEndIsBeforeOrderByStartDesc(Long bookerId, Pageable pageable, LocalDateTime localDateTime);

    // Find CURRENT bookings of booker
    Page<Booking> findAllByUserIdAndStartLessThanEqualAndEndGreaterThanEqualOrderById(Long bookerId, Pageable pageable, LocalDateTime from, LocalDateTime to);

    // Find FUTURE bookings of booker
    Page<Booking> findAllByUserIdAndStartIsAfterOrderByStartDesc(Long bookerId, Pageable pageable, LocalDateTime localDateTime);

    // Find bookings of booker with status
    Page<Booking> findAllByUserIdAndStatusIsOrderByStartDesc(Long bookerId, Pageable pageable, BookingStatus bookingStatus);

    // Find ALL bookings of owner
    Page<Booking> findAllByItemUserIdOrderByStartDesc(Long ownerId, Pageable pageable);

    // Find PAST bookings of owner
    Page<Booking> findAllByItemUserIdAndEndIsBeforeOrderByStartDesc(Long ownerId, Pageable pageable, LocalDateTime localDateTime);

    // Find CURRENT bookings of owner
    Page<Booking> findAllByItemUserIdAndStartLessThanEqualAndEndGreaterThanEqualOrderById(Long ownerId, Pageable pageable, LocalDateTime from, LocalDateTime to);

    // Find FUTURE bookings of owner
    Page<Booking> findAllByItemUserIdAndStartIsAfterOrderByStartDesc(Long ownerId, Pageable pageable, LocalDateTime localDateTime);

    // Find bookings of owner with status
    Page<Booking> findAllByItemUserIdAndStatusIsOrderByStartDesc(Long ownerId, Pageable pageable, BookingStatus bookingStatus);

    // Patch booking status
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Booking b " +
            "SET b.status = ?2 " +
            "WHERE b.id = ?1")
    void patch(Long bookingId, BookingStatus bookingStatus);
}