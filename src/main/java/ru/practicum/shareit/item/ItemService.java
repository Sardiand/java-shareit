package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {

    Item create(long userId, ItemDto itemDto);

    Item update(long userId, long itemId, ItemDto itemDto);

    Item getById(long itemId);

    List<Item> getAllUsersItems(long userId);

    List<Item> getAllByTextRequest(String request);
}
