package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.InMemoryRequestStorage;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
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
    public Item createItem(long userId, ItemDto itemDto) {
        if (inMemoryUserStorage.findById(userId) == null) {
            throwAndLogException(new NotFoundException("Пользователь с id равным " + userId + " не найден."));
        }
        Item item = itemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        id++;
        item.setId(id);
        return inMemoryItemStorage.add(item);
    }

    @Override
    public Item updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = getItemById(itemId);
        if (inMemoryUserStorage.findById(userId) == null) {
            throwAndLogException(new NotFoundException("Пользователь с id равным " + userId + " не найден."));
        }
        if (item.getOwnerId() != userId) {
            throwAndLogException(new ForbiddenException("Изменение полей предмета доступно только опубликовавшему " +
                    "его пользователю."));
        }
        return inMemoryItemStorage.edit(itemMapper.changeItemByDTO(item, itemDto));
    }

    @Override
    public Item getItemById(long itemId) {
        if (inMemoryItemStorage.findById(itemId) == null) {
            throwAndLogException(new NotFoundException("Предмет с id равным " + itemId + " не найден."));
        }
        return inMemoryItemStorage.findById(itemId);
    }

    @Override
    public List<Item> getAllUsersItems(long userId) {
        if (inMemoryUserStorage.findById(userId) == null) {
            throwAndLogException(new NotFoundException("Пользователь с id равным " + userId + " не найден."));
        }
        return inMemoryItemStorage.findAllByUserId(userId);
    }

    @Override
    public List<Item> getAllItemsByTextRequest(String request) {
        if (request.isBlank()) {
            return new ArrayList<>();
        }
        return inMemoryItemStorage.findAllByTextRequest(request);
    }

    private void throwAndLogException(RuntimeException exp) {
        log.error("Ошибка: " + exp.getMessage());
        throw exp;
    }

}
