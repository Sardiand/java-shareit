package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.InMemoryRequestStorage;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.util.UtilityStuff;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Validated
public class ItemServiceImpl implements ItemService {
    private long id = 0L;
    @Autowired
    private final ItemStorage inMemoryItemStorage;
    @Autowired
    private final UserStorage inMemoryUserStorage;
    private final ItemMapper itemMapper = new ItemMapper(new InMemoryRequestStorage());

    @Override
    public Item create(long userId, ItemDto itemDto) {
        inMemoryUserStorage.findById(userId).orElseThrow(() ->
                UtilityStuff.logError(new NotFoundException("Пользователь с id равным " + userId + " не найден.")));
        Item item = itemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        id++;
        item.setId(id);
        return inMemoryItemStorage.add(item);
    }

    @Override
    public Item update(long userId, long itemId, ItemDto itemDto) {
        Item item = getById(itemId);
        inMemoryUserStorage.findById(userId).orElseThrow(() ->
                UtilityStuff.logError(new NotFoundException("Пользователь с id равным " + userId + " не найден.")));
        if (item.getOwnerId() != userId) {
            throw UtilityStuff.logError(new ForbiddenException("Изменение полей предмета доступно только опубликовавшему " +
                    "его пользователю."));
        }
        return inMemoryItemStorage.edit(itemMapper.changeItemByDTO(item, itemDto));
    }

    @Override
    public Item getById(long itemId) {
        return inMemoryItemStorage.findById(itemId).orElseThrow(() ->
                UtilityStuff.logError(new NotFoundException("Предмет с id равным " + itemId + " не найден.")));
    }

    @Override
    public List<Item> getAllUsersItems(long userId) {
        inMemoryUserStorage.findById(userId).orElseThrow(() ->
                UtilityStuff.logError(new NotFoundException("Пользователь с id равным " + userId + " не найден.")));
        return inMemoryItemStorage.findAllByUserId(userId);
    }

    @Override
    public List<Item> getAllByTextRequest(String request) {
        if (request.isBlank()) {
            return new ArrayList<>();
        }
        return inMemoryItemStorage.findAllByTextRequest(request);
    }
}
