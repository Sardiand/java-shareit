package ru.practicum.shareit.user;

import lombok.Data;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Validated
public class User {
    private Long id;
    @NonNull
    @NotBlank(message = "Логин не может быть пустым.")
    private String name;
    @NonNull
    @Email(message = "Неверный формат адреса электронной почты.")
    private String email;
}
