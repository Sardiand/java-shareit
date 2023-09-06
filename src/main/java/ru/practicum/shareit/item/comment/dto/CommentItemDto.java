package ru.practicum.shareit.item.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentItemDto {
    private Long id;
    private String text;
    private LocalDateTime created;
    private String authorName;
    private Long itemId;
}
