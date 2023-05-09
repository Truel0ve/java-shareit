package ru.practicum.shareit.features.item.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.features.request.model.ItemRequest;
import ru.practicum.shareit.features.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    Long id;

    @NotNull
    @ManyToOne(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    User user;

    @NotBlank
    @Column(name = "item_name", nullable = false)
    String name;

    @NotBlank
    @Column(name = "description", length = 200, nullable = false)
    String description;

    @NotNull
    @Column(name = "available", nullable = false)
    Boolean available;

    @ManyToOne(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id")
    ItemRequest itemRequest;
}