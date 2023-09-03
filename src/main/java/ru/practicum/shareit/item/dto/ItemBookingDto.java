package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemBookingDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long ownerId;

    private Long lastBookingId;

    private Long lastBookerId;

    private Long nextBookingId;

    private Long nextBookerId;
}
