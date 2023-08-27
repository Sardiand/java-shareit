package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemCommentBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {

    Item create(long userId, ItemDto itemDto);

    Item update(long userId, long itemId, ItemDto itemDto);

    ItemCommentBookingDto getById(long userId, long itemId);

    List<ItemCommentBookingDto> getAllUsersItems(long userId);

    List<Item> getAllByTextRequest(String request);

    CommentDto createCommentToItem(long userId, long itemId, IncomingCommentDto incomingCommentDto);
}
