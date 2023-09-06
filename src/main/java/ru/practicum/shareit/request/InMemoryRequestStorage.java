package ru.practicum.shareit.request;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRequestStorage implements RequestStorage {
    private final Map<Long, ItemRequest> requests = new HashMap<>();

    public ItemRequest add(ItemRequest request) {
        requests.put(request.getId(), request);
        return request;
    }

    public ItemRequest findById(long requestId) {
        return requests.get(requestId);
    }
}
