package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(User requester, IncomingItemRequestDto dto) {
        return new ItemRequest(dto.getDescription(), requester,
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated());
    }

    public static void fillItemRequestDto(List<ItemRequestDto> dtos, List<ItemDto> items) {
        dtos.forEach(dto -> dto.getItems().addAll(
                items.stream().filter(i -> i.getRequestId().equals(dto.getId())).collect(Collectors.toList())
        ));
    }
}
