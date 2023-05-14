package ru.practicum.shareit.features.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.features.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Find all comments for item
    List<Comment> findByItemId(Long itemId);
}