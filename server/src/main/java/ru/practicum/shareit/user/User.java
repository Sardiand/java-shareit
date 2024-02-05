package ru.practicum.shareit.user;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "users", schema = "public")
@Data
@Validated
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @NotBlank(message = "Логин не может быть пустым.")
    private String name;

    @NonNull
    @Email(message = "Неверный формат адреса электронной почты.")
    private String email;

    public User(@NonNull String name, @NonNull String email) {
        this.name = name;
        this.email = email;
    }
}
