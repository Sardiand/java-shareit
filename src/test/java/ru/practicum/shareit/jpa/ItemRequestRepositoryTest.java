package ru.practicum.shareit.jpa;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.IncomingItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.toItemRequest;

@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository requestRepository;

    User user;
    IncomingItemRequestDto itemRequestDto;
    ItemRequest request;
    Pageable pageable;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.AAAAA");

    @BeforeEach
    void setAll() {
        user = new User("testUser", "testUser@user.com");
        userRepository.save(user);
        itemRequestDto = new IncomingItemRequestDto("test");
        request = requestRepository.save(toItemRequest(user, itemRequestDto));
        pageable = PageRequest.of(0, 10, Sort.by("created").descending());
    }

    @AfterEach
    void cleanAll() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getRequestDTOById() {
        ItemRequestDto dto = requestRepository.findDTOById(request.getId()).orElseThrow();

        assertEquals(request.getId(), dto.getId());
        assertEquals(request.getDescription(), dto.getDescription());
        assertEquals(request.getCreated(), dto.getCreated());
    }

    @Test
    void getAllRequestDtoExceptUser() {
        User secondUser = new User("testUser2", "testUser2@user.com");
        userRepository.save(secondUser);

        ItemRequest itemRequest = new ItemRequest("test2", secondUser, LocalDateTime.now());
        requestRepository.save(itemRequest);

        List<ItemRequestDto> requests = requestRepository.findAllItemRequestDto(secondUser.getId(),
                pageable);

        assertEquals(1, requests.size());
        assertEquals(requests.get(0).getDescription(), request.getDescription());
        assertEquals(requests.get(0).getId(), request.getId());
        assertEquals(requests.get(0).getCreated(), request.getCreated());
        assertTrue(requests.get(0).getItems().isEmpty());
    }

    @Test
    void getAllRequestDtoByUserId() {
        User secondUser = new User("testUser2", "testUser2@user.com");
        userRepository.save(secondUser);
        requestRepository.save(new ItemRequest("test2",
                secondUser, LocalDateTime.now()));
        ItemRequest thirdRequest = requestRepository.save(new ItemRequest("test3",
                user, LocalDateTime.now()));

        List<ItemRequestDto> requests = requestRepository.findAllByRequesterId(user.getId(),
                pageable);

        assertFalse(requests.isEmpty());
        assertEquals(2, requests.size());
        assertEquals(requests.get(1).getId(), request.getId());
        assertEquals(requests.get(0).getId(), thirdRequest.getId());
        assertEquals(requests.get(1).getDescription(), request.getDescription());
        assertEquals(requests.get(0).getDescription(), thirdRequest.getDescription());
        assertEquals(requests.get(1).getCreated().format(formatter), request.getCreated()
                .format(formatter));
        assertEquals(requests.get(0).getCreated().format(formatter), thirdRequest.getCreated()
                .format(formatter));
    }
}
