package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemStorage {

    Item add(Item item);

    Item edit(Item item);

    List<Item> findAllByUserId(long userId);

    Item findById(long itemId);

    List<Item> findAllByTextRequest(String request);

    void deleteAllByUserId(long userId);
}
