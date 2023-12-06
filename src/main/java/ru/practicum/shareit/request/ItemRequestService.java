package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.IncomingItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Service
public interface ItemRequestService {

    ItemRequestDto create(long userId, IncomingItemRequestDto dto);

    ItemRequestDto getById(long userId, long requestId);

    List<ItemRequestDto> getAll(long userId, int from, int size);

    List<ItemRequestDto> getAllByRequesterId(long requesterId, int from, int size);

}
