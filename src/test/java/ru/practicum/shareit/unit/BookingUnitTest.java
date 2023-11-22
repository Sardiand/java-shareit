package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingUnitTest {

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository = mock(UserRepository.class);
    @Mock
    ItemRepository itemRepository;
    User user;
    User user2;
    Item item;
    Item item2;
    Booking firstBooking;
    Booking secondBooking;
    BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        user = new User("user1", "user1@user.com");
        user2 = new User("user2", "user2@user.com");
        user.setId(1L);
        user2.setId(2L);

        item = new Item("item1", "itemD1", false, null);
        item2 = new Item("item2", "itemD2", true, null);
        item.setId(1L);
        item.setOwner(user);
        item2.setId(2L);
        item2.setOwner(user2);
        firstBooking = new Booking(1L, null, null, item, user, Status.APPROVED);
        secondBooking = new Booking(2L, null, null, item2, user2, Status.WAITING);
        bookingDto = new BookingDto(1L,
                LocalDateTime.of(2023, 10, 13, 12, 0),
                LocalDateTime.of(2023, 10, 12, 12, 0));
    }

    @Test
    void addBooking_whenNoUserFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(anyLong(), null));
    }

    @Test
    void addBooking_whenNoItemFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.create(user.getId(), bookingDto));
        verify(bookingRepository, never()).save(firstBooking);
    }

    @Test
    void addBooking_whenUserIsAnOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () ->
                bookingService.create(user.getId(), bookingDto));
        verify(bookingRepository, never()).save(firstBooking);
    }

    @Test
    void addBooking_whenItemNotAvailable() {
        user.setId(2L);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () ->
                bookingService.create(anyLong(), bookingDto));
        verify(bookingRepository, never()).save(firstBooking);
    }

    @Test
    void updateBooking_whenBookingNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.update(user.getId(), anyLong(), true));
        verify(bookingRepository, never()).save(firstBooking);
    }

    @Test
    void updateBooking_whenNotItemOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(secondBooking));

        assertThrows(NotFoundException.class, () ->
                bookingService.update(user.getId(), secondBooking.getId(), true));
        verify(bookingRepository, never()).save(firstBooking);
    }

    @Test
    void getBookings_whenNoUserFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.getAllByUserIdAndState(anyLong(), State.ALL, 0, 10));
        verify(bookingRepository, never()).findAllByBooker_IdOrderByStartDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void getBookingsForOwnedItems_whenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.getAllBookingsOfUsersItemsByUserId(anyLong(), State.ALL, 0, 10));
        verify(bookingRepository, never()).findAllByItem_IdInOrderByStartDesc(anyList(), any(Pageable.class));
    }

    @Test
    void getBooking_whenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.getById(anyLong(), 1L));
        verify(bookingRepository, never()).findById(anyLong());
    }

    @Test
    void getBooking_whenUserNeitherBookerNorOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(secondBooking));

        assertThrows(NotFoundException.class, () ->
                bookingService.getById(user.getId(), firstBooking.getId()));
        verify(bookingRepository, atMostOnce()).findById(anyLong());
    }
}
