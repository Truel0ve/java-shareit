package ru.practicum.shareit.features.booking.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.features.item.model.Item;
import ru.practicum.shareit.features.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", schema = "public")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    Long id;

    @Column(name = "start_date", nullable = false)
    LocalDateTime start;

    @Column(name = "end_date", nullable = false)
    LocalDateTime end;

    @Enumerated(EnumType.STRING)
    BookingStatus status;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "booker_id", nullable = false)
    User user;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    Item item;
}