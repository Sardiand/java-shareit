package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemCommentBookingDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingItemDto lastBooking;

    private BookingItemDto nextBooking;

    private final List<CommentDto> comments = new ArrayList<>();
}
