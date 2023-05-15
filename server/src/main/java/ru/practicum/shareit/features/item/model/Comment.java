package ru.practicum.shareit.features.item.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.features.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    Long id;

    @Column(name = "content", length = 200, nullable = false)
    String text;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    User user;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    Item item;

    @Column(name = "creation_date", nullable = false)
    LocalDateTime created;
}