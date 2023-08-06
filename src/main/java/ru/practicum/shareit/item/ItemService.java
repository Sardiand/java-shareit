package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {

    Item createItem(long userId, ItemDto itemDto);

    Item updateItem(long userId, long itemId, ItemDto itemDto);

    Item getItemById(long itemId);

    List<Item> getAllUsersItems(long userId);

    List<Item> getAllItemsByTextRequest(String request);
}
