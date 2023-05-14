package ru.practicum.shareit.features.booking;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ArgumentNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongStateArgumentException;
import ru.practicum.shareit.features.booking.model.*;
import ru.practicum.shareit.features.item.ItemService;
import ru.practicum.shareit.features.item.model.Item;
import ru.practicum.shareit.features.user.UserService;
import ru.practicum.shareit.features.user.model.User;
import ru.practicum.shareit.utility.PageManager;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        User booker = booking.getUser();
        User ownerItem = booking.getItem().getUser();
        if (booker.getId().equals(userId) || ownerItem.getId().equals(userId)) {
            return mapToBookingDto(booking);
        } else {
            throw new ArgumentNotFoundException("User id=" + userId + " is not the booker/owner of item id=" +
                    booking.getItem().getId() + " or the specified item does not exist");
        }
    }

    @Override
    public List<BookingDto> getBookingsOfBooker(Long bookerId, String state, Integer from, Integer size) {
        userService.validateUserId(bookerId);
        Pageable pageable = PageManager.getPageable(from, size);
        switch (getState(state)) {
            case PAST:
                return mapToBookingDtoList(bookingRepository.findAllByUserIdAndEndIsBeforeOrderByStartDesc(bookerId, pageable, LocalDateTime.now()));
            case CURRENT:
                return mapToBookingDtoList(bookingRepository
                        .findAllByUserIdAndStartLessThanEqualAndEndGreaterThanEqualOrderById(bookerId, pageable, LocalDateTime.now(), LocalDateTime.now()));
            case FUTURE:
                return mapToBookingDtoList(bookingRepository.findAllByUserIdAndStartIsAfterOrderByStartDesc(bookerId, pageable, LocalDateTime.now()));
            case WAITING:
                return mapToBookingDtoList(bookingRepository.findAllByUserIdAndStatusIsOrderByStartDesc(bookerId, pageable, BookingStatus.WAITING));
            case REJECTED:
                return mapToBookingDtoList(bookingRepository.findAllByUserIdAndStatusIsOrderByStartDesc(bookerId, pageable, BookingStatus.REJECTED));
            default:
                return mapToBookingDtoList(bookingRepository.findAllByUserIdOrderByStartDesc(bookerId, pageable));
        }
    }

    @Override
    public List<BookingDto> getBookingsOfOwner(Long ownerId, String state, Integer from, Integer size) {
        userService.validateUserId(ownerId);
        Pageable pageable = PageManager.getPageable(from, size);
        switch (getState(state)) {
            case PAST:
                return mapToBookingDtoList(bookingRepository.findAllByItemUserIdAndEndIsBeforeOrderByStartDesc(ownerId, pageable, LocalDateTime.now()));
            case CURRENT:
                return mapToBookingDtoList(bookingRepository
                        .findAllByItemUserIdAndStartLessThanEqualAndEndGreaterThanEqualOrderById(ownerId, pageable, LocalDateTime.now(), LocalDateTime.now()));
            case FUTURE:
                return mapToBookingDtoList(bookingRepository.findAllByItemUserIdAndStartIsAfterOrderByStartDesc(ownerId, pageable, LocalDateTime.now()));
            case WAITING:
                return mapToBookingDtoList(bookingRepository.findAllByItemUserIdAndStatusIsOrderByStartDesc(ownerId, pageable, BookingStatus.WAITING));
            case REJECTED:
                return mapToBookingDtoList(bookingRepository.findAllByItemUserIdAndStatusIsOrderByStartDesc(ownerId, pageable, BookingStatus.REJECTED));
            default:
                return mapToBookingDtoList(bookingRepository.findAllByItemUserIdOrderByStartDesc(ownerId, pageable));
        }
    }

    @Transactional
    @Override
    public BookingDto create(Long bookerId, BookingShortDto bookingDto) {
        Item item = itemService.getItemById(bookingDto.getItemId());
        if (isOwnerOfItem(bookerId, item)) {
            throw new ArgumentNotFoundException("Unable to create booking. The specified user id=" + bookerId +
                    " is the owner of item id=" + item.getId());
        }
        validateItemAvailable(item);
        validateTimestamps(bookingDto);
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setUser(userService.getUserById(bookerId));
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return mapToBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto patch(Long ownerId, Long bookingId, Boolean approved) {
        Booking booking = getBookingById(bookingId);
        validateBookingStatus(booking.getStatus());
        if (!isOwnerOfItem(ownerId, booking.getItem())) {
            throw new ArgumentNotFoundException("Unable to approve booking. The specified user id=" + ownerId +
                    " is not the owner of item id=" + booking.getItem().getId());
        }
        if (Boolean.TRUE.equals(approved)) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.patch(bookingId, booking.getStatus());
        return mapToBookingDto(getBookingById(bookingId));
    }

    private Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ArgumentNotFoundException("The specified booking id=" + id + " does not exist"));
    }

    private BookingDto mapToBookingDto(Booking booking) {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        bookingDto.setBooker(userService.getUserShortById(booking.getUser().getId()));
        bookingDto.setItem(itemService.getItemShortById(booking.getItem().getId()));
        return bookingDto;
    }

    private List<BookingDto> mapToBookingDtoList(Page<Booking> bookings) {
        return bookings.map(this::mapToBookingDto).getContent();
    }

    private boolean isOwnerOfItem(Long ownerId, Item item) {
        return item.getUser().getId().equals(ownerId);
    }

    private void validateItemAvailable(Item item) {
        if (item.getAvailable().equals(false)) {
            throw new ValidationException("The specified item id=" + item.getId() + " is not available");
        }
    }

    private void validateTimestamps(BookingShortDto bookingDto) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (end.isEqual(start)) {
            throw new ValidationException("Start and end dates can`t be the same");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Wrong start data value");
        }
        if (end.isBefore(LocalDateTime.now()) || end.isBefore(start)) {
            throw new ValidationException("Wrong end data value");
        }
    }

    private void validateBookingStatus(BookingStatus bookingStatus) {
        if (!bookingStatus.equals(BookingStatus.WAITING)) {
            throw new ValidationException("Booking is already " + bookingStatus + " by owner of item");
        }
    }

    private State getState(String state) {
        if (!EnumUtils.isValidEnum(State.class, state)) {
            throw new WrongStateArgumentException("Unknown state: " + state);
        } else {
            return State.valueOf(state);
        }
    }
}