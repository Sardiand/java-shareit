package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentItemDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.util.UtilityStuff;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingItemDto;

@RequiredArgsConstructor
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item(itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                null);
        UtilityStuff.validateItem(item);

        return item;
    }

    public static Item changeItemByDTO(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        UtilityStuff.validateItem(item);

        return item;
    }

    public static ItemCommentBookingDto toItemCommentBookingDto(long userId, ItemBookingDto itemBookingDto,
                                                                List<CommentDto> commentDtos) {
        ItemCommentBookingDto itemCommentBookingDto = new ItemCommentBookingDto(itemBookingDto.getId(),
                itemBookingDto.getName(),
                itemBookingDto.getDescription(),
                itemBookingDto.getAvailable(),
                null,
                null);
        if (userId == itemBookingDto.getOwnerId()) {
            itemCommentBookingDto.setLastBooking(toBookingItemDto(itemBookingDto.getLastBookingId(),
                    itemBookingDto.getLastBookerId()));
            if (itemBookingDto.getNextBookingId() != null) {
                itemCommentBookingDto.setNextBooking(toBookingItemDto(itemBookingDto.getNextBookingId(),
                        itemBookingDto.getNextBookerId()));
            }
        }
        itemCommentBookingDto.getComments().addAll(commentDtos);

        return itemCommentBookingDto;
    }

    public static List<ItemCommentBookingDto> toItemCommentBookingDtos(long userId, List<ItemBookingDto> items,
                                                                       List<CommentItemDto> commentDtos) {
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        return items.stream()
                .map(itemBookingDto -> {
                    ItemCommentBookingDto itemCommentBookingDto;
                    List<CommentDto> comments = new ArrayList<>();
                    if (!commentDtos.isEmpty()) {
                        comments.addAll(commentDtos.stream()
                                .filter(i -> i.getItemId().equals(itemBookingDto.getId()))
                                .map(CommentMapper::toCommentDto)
                                .collect(Collectors.toList()));
                    }

                    if (userId == itemBookingDto.getOwnerId()) {
                        itemCommentBookingDto = new ItemCommentBookingDto(itemBookingDto.getId(),
                                itemBookingDto.getName(),
                                itemBookingDto.getDescription(),
                                itemBookingDto.getAvailable(),
                                toBookingItemDto(itemBookingDto.getLastBookingId(), itemBookingDto.getLastBookerId()),
                                toBookingItemDto(itemBookingDto.getNextBookingId(), itemBookingDto.getNextBookerId()));
                    } else {
                        itemCommentBookingDto = new ItemCommentBookingDto(itemBookingDto.getId(),
                                itemBookingDto.getName(),
                                itemBookingDto.getDescription(),
                                itemBookingDto.getAvailable(),
                                null,
                                null);
                    }
                    itemCommentBookingDto.getComments().addAll(comments);

                    return itemCommentBookingDto;
                })
                .collect(Collectors.toList());
    }
}
