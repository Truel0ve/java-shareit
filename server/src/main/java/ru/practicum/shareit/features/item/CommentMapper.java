package ru.practicum.shareit.features.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.features.item.model.Comment;
import ru.practicum.shareit.features.item.model.CommentDto;

@UtilityClass
public class CommentMapper {

    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getUser().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setCreated(commentDto.getCreated());
        return comment;
    }
}