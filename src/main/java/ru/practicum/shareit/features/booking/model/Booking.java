package ru.practicum.shareit.features.booking.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.features.item.model.Item;
import ru.practicum.shareit.features.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", schema = "public")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    Long id;

    @NotNull
    @Column(name = "start_date", nullable = false)
    LocalDateTime start;

    @NotNull
    @Column(name = "end_date", nullable = false)
    LocalDateTime end;

    @NotNull
    @Enumerated(EnumType.STRING)
    BookingStatus status;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booker_id", nullable = false)
    User user;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    Item item;
}