package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemCommentBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemUnitTest {

        @InjectMocks
        private ItemServiceImpl itemService;
        @Mock
        ItemRepository itemRepository;
        @Mock
        UserRepository userRepository;
        @Mock
        BookingRepository bookingRepository;
        @Mock
        CommentRepository commentRepository;
        @Mock
        ItemRequestRepository requestRepository;
        User user;
        Item item;
        ItemDto itemDto;
        Booking lastBooking;
        Booking nextBooking;
        ItemRequest request;


        @BeforeEach
        void setUp() {
            itemService = new ItemServiceImpl(itemRepository, userRepository, commentRepository, bookingRepository,
                    requestRepository);
            user = new User("user1", "user1@user.com");
            user.setId(1L);
            itemDto = new ItemDto("dto", "dto", true, null);
            item = Item.builder()
                    .id(1L)
                    .name("item")
                    .description("itemD")
                    .owner(user)
                    .available(false)
                    .build();
            lastBooking = Booking.builder()
                    .id(1L)
                    .item(item)
                    .booker(user)
                    .build();
            nextBooking = Booking.builder()
                    .id(2L)
                    .item(item)
                    .booker(user)
                    .build();
            request = ItemRequest.builder().id(1L).build();
        }

        @Test
        void addItem_whenUserNotFound() {
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () ->
                    itemService.create(anyLong(), itemDto));
            verify(itemRepository, never()).save(item);
        }

        @Test
        void addComment_whenNoValidBookingFound() {
            when(bookingRepository.findPreviousByItemIdAndUserId(anyLong(), anyLong(), any(Status.class)))
                    .thenReturn(Collections.emptyList());
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(user));
            when(itemRepository.findById(anyLong()))
                    .thenReturn(Optional.of(item));

            assertThrows(BadRequestException.class, () ->
                    itemService.createCommentToItem(1L, 1L, null));
            verify(commentRepository, never()).save(new Comment());
        }

        @Test
        void updateItem_whenNoItemFound() {
            when(itemRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());


            assertThrows(NotFoundException.class, () ->
                    itemService.update(1L, 1L, null));
            verify(itemRepository, never()).save(item);
        }

        @Test
        void updateItem_whenUserNotAnOwner() {
            when(itemRepository.findById(anyLong()))
                    .thenReturn(Optional.of(item));

            assertThrows(NotFoundException.class, () ->
                    itemService.update(2L, 1L, null));
            verify(itemRepository, never()).save(item);
        }

        @Test
        void getItemById_whenUserNotFound() {
            assertThrows(NotFoundException.class, () ->
                    itemService.getById(anyLong(), 1L));
        }

        @Test
        void getItemById_whenItemNotFound() {
            when(userRepository.existsById(anyLong()))
                    .thenReturn(true);

            assertThrows(NotFoundException.class, () ->
                     itemService.getById(user.getId(), anyLong()));
             verify(itemRepository, atMostOnce()).findById(anyLong());
        }

        @Test
        void getItemById_whenUserNotOwner_withCommentsEmpty() {
            user.setId(99L);
            when(userRepository.existsById(anyLong()))
                    .thenReturn(true);
            when(itemRepository.findItemBookingDtoById(anyLong()))
                    .thenReturn(Optional.of(ItemMapper.toItemBookingDto(item, user.getId())));

            ItemCommentBookingDto dto = itemService.getById(user.getId(), item.getId());
            assertNotNull(dto);
            assertEquals(item.getId(), dto.getId());
            assertNull(dto.getLastBooking());
            assertNull(dto.getNextBooking());
            assertNotNull(dto.getComments());
            assertEquals(0, dto.getComments().size());
        }

        @Test
        void searchItems_whenNoItemsFound() {
            when(itemRepository.findAllByTextRequest(anyString(), any(Pageable.class)))
                    .thenReturn(Collections.emptyList());

            List<Item> items = itemService.getAllByTextRequest("fargo", 0, 2);
            verify(itemRepository, atMostOnce()).findAllByTextRequest(anyString(), any(Pageable.class));
            assertNotNull(items);
            assertEquals(0, items.size());
        }

        @Test
        void getItems_whenNoUserFound() {
            assertThrows(NotFoundException.class, () -> itemService.getAllUsersItems(anyLong(), 0, 2));
            verify(itemRepository, never()).findAllItemBookingDto(anyLong(), any(Pageable.class));
        }

        @Test
        void getItems_whenNoItemsFound() {
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(user));

            List<ItemCommentBookingDto> items = itemService.getAllUsersItems(user.getId(), 0, 2);
            assertNotNull(items);
            assertEquals(0, items.size());
        }

        @Test
        void testItemMapper() {
            assertThrows(NullPointerException.class, () -> ItemMapper.toItemDto(null));
            assertThrows(NullPointerException.class, () ->
                    ItemMapper.toItemCommentBookingDto(1L, null, Collections.emptyList()));

            ItemDto withoutRequest = ItemMapper.toItemDto(item);
            assertNotNull(withoutRequest);
            assertNull(withoutRequest.getRequestId());
        }
}
