package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.IncomingItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.toItemRequest;

@ExtendWith(MockitoExtension.class)
public class ItemRequestUnitTest {
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    User user;
    IncomingItemRequestDto dto;


    @BeforeEach
    void setAll() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository,
                userRepository, itemRepository);
        dto = new IncomingItemRequestDto("test");
        user = new User("test", "test@user.com");
        user.setId(1L);
    }

    @Test
    void createWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.create(user.getId(), dto));
        verify(itemRequestRepository, never()).save(toItemRequest(user, dto));
    }

    @Test
    void getByIdWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getById(1L, 1L));
        verify(itemRequestRepository, never()).findDTOById(1L);
    }

    @Test
    void getByIdWhenRequestNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findDTOById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getById(1L, 1L));
        verify(itemRequestRepository, atMostOnce()).findDTOById(1L);
    }

    @Test
    void getAllWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getAll(1L, 0, 5));
    }

    @Test
    void getAllByRequesterIdWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getAllByRequesterId(1L, 0, 5));
    }

    @Test
    void getAllByRequesterIdWithoutRequests() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterId(anyLong(), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> dtos = itemRequestService.getAllByRequesterId(1L, 0, 5);

        assertTrue(dtos.isEmpty());
    }
}
