package ru.practicum.shareit.item.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
public class IncomingCommentDto {
    @NonNull
    @NotBlank(message = "Комментарий не может быть пустым.")
    String text;
}
