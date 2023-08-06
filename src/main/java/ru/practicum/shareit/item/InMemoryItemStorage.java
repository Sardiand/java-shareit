package ru.practicum.shareit.item;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item add(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item edit(Item item) {
        items.replace(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> findAllByUserId(long userId) {
        return items.values().stream().filter(item -> item.getOwnerId() == userId).collect(Collectors.toList());
    }

    @Override
    public Item findById(long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> findAllByTextRequest(String request) {
        return items.values().stream().filter(item -> (StringUtils.containsIgnoreCase(item.getName(), request) |
                        StringUtils.containsIgnoreCase(item.getDescription(), request) && item.getAvailable()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllByUserId(long userId) {
        for (Item item : List.copyOf(items.values())) {
            if (item.getOwnerId() == userId) {
                items.remove(item.getId());
            }
        }
    }
}
