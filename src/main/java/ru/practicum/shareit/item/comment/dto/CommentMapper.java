package ru.practicum.shareit.item.comment.dto;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment fromIncomingDto(User user, Item item, IncomingCommentDto incomingCommentDto) {
        return new Comment(incomingCommentDto.getText(), LocalDateTime.now(), item, user);
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getCreated(), comment.getAuthor().getName());
    }
}
