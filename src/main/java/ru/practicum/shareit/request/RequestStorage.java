package ru.practicum.shareit.request;

import org.springframework.stereotype.Repository;

@Repository
public interface RequestStorage {

    ItemRequest add(ItemRequest request);

    ItemRequest findById(long requestId);
}
