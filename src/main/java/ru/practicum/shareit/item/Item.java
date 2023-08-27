package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class Item {
    private Long id;

    @NotBlank(message = "Поле name не может быть пустым.")
    @NonNull
    private String name;

    @NonNull
    @NotBlank(message = "Поле description не может быть пустым.")
    private String description;
    @NonNull
    private Boolean available;
    private Long ownerId;
    private ItemRequest request;

    public Item(@NonNull String name, @NonNull String description, @NonNull Boolean isAvailable, ItemRequest request) {
        this.name = name;
        this.description = description;
        this.available = isAvailable;
        this.request = request;
    }
}
