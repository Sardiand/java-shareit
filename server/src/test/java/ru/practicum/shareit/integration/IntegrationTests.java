package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemCommentBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.IncomingItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"spring.datasource.driver-class-name=org.h2.Driver"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTests {

    @Autowired
    private final UserService userServiceImpl;

    @Autowired
    private final ItemService itemServiceImpl;

    @Autowired
    private final BookingService bookingServiceImpl;

    @Autowired
    private final ItemRequestService itemRequestServiceImpl;

    UserDto userDto1 = new UserDto("User1", "user1@user.com");
    UserDto userDto2 = new UserDto("User2", "user2@user.com");
    UserDto userDto3 = new UserDto("User3", "user3@user.com");

    ItemDto itemDto1 = new ItemDto("Мьёльнир", "Настоящий молот Тора", true, null);
    ItemDto itemDto2 = new ItemDto("Гунгнир", "Копьё Одина", true, null);
    ItemDto itemDto3 = new ItemDto("Драупнир", "Волшебное кольцо Одина", true, null);


    @Test
    @Order(1)
    void contextLoads() {
        assertNotNull(userServiceImpl);
        assertNotNull(itemServiceImpl);
        assertNotNull(bookingServiceImpl);
        assertNotNull(itemRequestServiceImpl);
    }

    @Test
    @Order(2)
    void userTest() {
        User user = userServiceImpl.create(userDto1);

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals(userDto1.getEmail(), user.getEmail());
        assertEquals(userDto1.getName(), user.getName());

        User receivedUser = userServiceImpl.getById(1L);

        assertNotNull(receivedUser);
        assertEquals(user.getId(), receivedUser.getId());
        assertEquals(user.getEmail(), receivedUser.getEmail());
        assertEquals(user.getName(), receivedUser.getName());

        UserDto updateEmail = new UserDto(null, "update@user.com");
        User updatedUser = userServiceImpl.update(1L, updateEmail);

        assertNotNull(updatedUser);
        assertEquals(updateEmail.getEmail(), updatedUser.getEmail());

        userServiceImpl.create(userDto2);
        userServiceImpl.create(userDto3);
        List<User> users = userServiceImpl.getAll();

        assertEquals(3, users.size());
        assertEquals(2L, users.get(1).getId());

        userServiceImpl.deleteById(2L);

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            userServiceImpl.getById(2L);
        });
        assertEquals("User с id 2 не найден.", thrown.getMessage());
    }

    @Test
    @Order(3)
    void itemTest() {
        ItemDto item = itemServiceImpl.create(1L, itemDto1);

        assertEquals(1L, item.getId());
        assertEquals("Мьёльнир", item.getName());
        assertEquals("Настоящий молот Тора", item.getDescription());
        assertTrue(item.getAvailable());

        ItemCommentBookingDto receivedItem = itemServiceImpl.getById(1L, 1L);

        assertEquals(1L, receivedItem.getId());
        assertEquals("Мьёльнир", receivedItem.getName());
        assertEquals("Настоящий молот Тора", receivedItem.getDescription());
        assertTrue(receivedItem.getAvailable());

        ItemDto updatedItem = itemServiceImpl.update(1L, 1L, new ItemDto(null,
                "Восстановленный молот Тора (б/у)", null, null));

        assertEquals(1L, updatedItem.getId());
        assertEquals("Мьёльнир", updatedItem.getName());
        assertEquals("Восстановленный молот Тора (б/у)", updatedItem.getDescription());
        assertTrue(updatedItem.getAvailable());

        itemServiceImpl.create(1L, itemDto2);
        itemServiceImpl.create(1L, itemDto3);
        List<ItemCommentBookingDto> items = itemServiceImpl.getAllUsersItems(1L, 1, 5);

        assertEquals(3, items.size());
        assertEquals(2L, items.get(1).getId());
    }

    @Test
    @Order(4)
    void bookingTest() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 12, 9, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 13, 9, 0);
        BookingDto bookingDto = new BookingDto(1L, start, end);
        Booking booking = bookingServiceImpl.create(3L, bookingDto);

        assertEquals(1L, booking.getId());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(1L, booking.getItem().getId());
        assertEquals(3L, booking.getBooker().getId());

        Booking approvedBooking = bookingServiceImpl.update(1L, 1L, true);

        assertEquals(Status.APPROVED, approvedBooking.getStatus());

        BookingDto bookingDto2 = new BookingDto(2L, start.minusDays(1), end);
        BookingDto bookingDto3 = new BookingDto(3L, start.minusDays(2), end);
        bookingServiceImpl.create(3L, bookingDto2);
        bookingServiceImpl.create(3L, bookingDto3);

        List<Booking> bookings = bookingServiceImpl.getAllByUserIdAndState(3L, State.ALL, 0, 5);

        assertFalse(bookings.isEmpty());
        assertEquals(3, bookings.size());
        assertEquals(3L, bookings.get(2).getId());

        bookings = bookingServiceImpl.getAllBookingsOfUsersItemsByUserId(1L, State.ALL, 0, 5);

        assertFalse(bookings.isEmpty());
        assertEquals(3, bookings.size());
    }

    @Order(4)
    @Test
    void itemRequestTest() {
        IncomingItemRequestDto requestDto = new IncomingItemRequestDto("Нужно кольцо, чтобы править всеми.");
        ItemRequestDto request = itemRequestServiceImpl.create(1L, requestDto);

        assertEquals(1L, request.getId());
        assertEquals(requestDto.getDescription(), request.getDescription());

        ItemRequestDto receivedRequest = itemRequestServiceImpl.getById(1L, 1L);

        assertEquals(1L, receivedRequest.getId());
        assertEquals(1L, receivedRequest.getId());
        assertEquals(requestDto.getDescription(), receivedRequest.getDescription());

        ItemDto itemDto4 = itemServiceImpl.create(3L, new ItemDto("Моя прелесть",
                "Старинное кольцо из Мордора", true, 1L));

        assertEquals(4L, itemDto4.getId());
        assertEquals(1L, itemDto4.getRequestId());

        requestDto.setDescription("Нужна пара орлов для путешествия.");
        itemRequestServiceImpl.create(3L, requestDto);

        requestDto.setDescription("Нужен лук, желательно эльфийский.");
        itemRequestServiceImpl.create(3L, requestDto);

        requestDto.setDescription("Нужен топор гномской работы.");
        itemRequestServiceImpl.create(3L, requestDto);

        List<ItemRequestDto> requests = itemRequestServiceImpl.getAll(1L, 0, 5);

        assertFalse(requests.isEmpty());
        assertEquals(3, requests.size());
        assertEquals(4L, requests.get(0).getId());
        assertEquals(requestDto.getDescription(), requests.get(0).getDescription());
    }

    @Order(5)
    @Test
    void commentsTest() throws InterruptedException {
        bookingServiceImpl.create(3L, new BookingDto(1L, LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2)));
        bookingServiceImpl.update(1, 4L, true);
        Thread.sleep(3000);

        IncomingCommentDto incomingComment = new IncomingCommentDto("Хороший молот, но поднять не смог.");
        itemServiceImpl.createCommentToItem(3L, 1L, incomingComment);
        ItemCommentBookingDto itemComment = itemServiceImpl.getById(1L, 1L);
        List<CommentDto> comments = itemComment.getComments();

        assertFalse(comments.isEmpty());
        assertEquals(1, comments.size());
        assertEquals(1L, comments.get(0).getId());
        assertEquals(incomingComment.getText(), comments.get(0).getText());
    }

    @Order(6)
    @Test
    void getItemsByRequest() {
        List<Item> items = itemServiceImpl.getAllByTextRequest("кольцо", 0, 5);

        assertFalse(items.isEmpty());
        assertEquals(2, items.size());
        assertEquals(3L, items.get(0).getId());
        assertEquals(itemDto3.getName(), items.get(0).getName());
    }
}
