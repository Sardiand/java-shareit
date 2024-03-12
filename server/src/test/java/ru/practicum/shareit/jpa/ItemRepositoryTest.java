package ru.practicum.shareit.jpa;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User("testOwner", "owner@user.com");
        userRepository.save(owner);

        item = new Item("Грааль", "Святой", true, null);
        item.setOwner(owner);
        itemRepository.save(item);
    }

    @AfterEach
    void cleanAll() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findItemsByTextRequest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = itemRepository.findAllByTextRequest("грааль", pageable);

        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());

        List<Item> items2 = itemRepository.findAllByTextRequest("гРааЛь", pageable);

        assertEquals(1, items2.size());
        assertEquals(item.getId(), items2.get(0).getId());
    }

    @Test
    void findItemIds() {
        List<Long> itemIds = itemRepository.findAllItemIdsByUserId(owner.getId());
        assertEquals(1, itemIds.size());
        assertEquals(item.getId(), itemIds.get(0));
    }
}

