package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.IncomingItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static ru.practicum.shareit.request.dto.ItemRequestMapper.*;
import static ru.practicum.shareit.util.UtilityStuff.logError;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService{
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(long userId, IncomingItemRequestDto dto) {
       User user = userRepository.findById(userId).orElseThrow(() ->
                logError(new NotFoundException("User с id " + userId + " не найден.")));
        ItemRequest request = itemRequestRepository.save(toItemRequest(user, dto));
        return toItemRequestDto(request);
    }

    @Override
    public ItemRequestDto getById(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() ->
                logError(new NotFoundException("User с id " + userId + " не найден.")));
        ItemRequestDto dto = itemRequestRepository.findById(requestId).orElseThrow(() ->
                logError(new NotFoundException("Запрос с id " + requestId + " не найден.")));
        dto.getItems().addAll(itemRepository.findAllByRequestId(requestId));

        return dto;
    }

    @Override
    public List<ItemRequestDto> getAll(long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() ->
                logError(new NotFoundException("User с id " + userId + " не найден.")));
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").descending());
        List<ItemRequestDto> dtos = itemRequestRepository.findAllItemRequestDto(userId, pageable);
        if (dtos.isEmpty()) {
            return new ArrayList<>();
        } else {
            List<ItemDto> items = itemRepository.findAllWithRequestId(userId);
            if (!items.isEmpty()) {
                fillItemRequestDto(dtos, items);
            }
        }

        return dtos;
    }

    @Override
    public List<ItemRequestDto> getAllByRequesterId(long requesterId, int from, int size) {
        userRepository.findById(requesterId).orElseThrow(() ->
                logError(new NotFoundException("User с id " + requesterId + " не найден.")));
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").descending());
        List<ItemRequestDto> dtos = itemRequestRepository.findAllByRequesterId(requesterId, pageable);
        if(dtos.isEmpty()) {
            return new ArrayList<>();
        } else {
            List<ItemDto> items = itemRepository.findAllByRequesterId(requesterId);
            if (!items.isEmpty()) {
                fillItemRequestDto(dtos, items);
            }
        }

        return dtos;
    }
}
