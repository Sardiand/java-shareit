package ru.practicum.shareit.item.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IncomingCommentDto {
    @NonNull
    @NotBlank(message = "Комментарий не может быть пустым.")
    String text;
}
