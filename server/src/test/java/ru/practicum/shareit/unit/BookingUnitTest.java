package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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
    Pageable pageable;

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

        pageable = PageRequest.of(0, 10, Sort.by("start").descending());
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
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(secondBooking));

        NotFoundException exp = assertThrows(NotFoundException.class, () ->
                bookingService.update(user.getId(), secondBooking.getId(), true));
        assertEquals("У пользователя с id " + user.getId()
                + " нет доступа к бронированию с id " + secondBooking.getId() + ".", exp.getMessage());
        verify(bookingRepository, never()).save(firstBooking);
    }

    @Test
    void updateBooking_whenStatusIsAlreadyApproved() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(firstBooking));

        BadRequestException exp = assertThrows(BadRequestException.class, () ->
                bookingService.update(user.getId(), firstBooking.getId(), true));
        assertEquals("Бронирование с id " + firstBooking.getId()
                + " уже находится в статусе " + firstBooking.getStatus() + ".", exp.getMessage());
        verify(bookingRepository, never()).save(firstBooking);
    }

    @Test
    void getBookings_whenNoUserFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.getAllByUserIdAndState(anyLong(), State.ALL, 0, 10));
        verify(bookingRepository, never()).findAllByBooker_IdOrderByStartDesc(anyLong(),
                any(Pageable.class));
    }

    @Test
    void getAllCurrentBookingsByUserIdAndState() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllCurrentByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(firstBooking));

        List<Booking> bookings = bookingService.getAllByUserIdAndState(user.getId(), State.CURRENT, 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void getAllPreviousBookingsByUserIdAndState() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllPreviousByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(firstBooking));

        List<Booking> bookings = bookingService.getAllByUserIdAndState(user.getId(), State.PAST, 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void getAllFutureBookingsByUserIdAndState() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllUpcomingByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(firstBooking));

        List<Booking> bookings = bookingService.getAllByUserIdAndState(user.getId(), State.FUTURE, 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void getAllWaitingBookingsByUserIdAndState() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(anyLong(),
                any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(firstBooking));

        List<Booking> bookings = bookingService.getAllByUserIdAndState(user.getId(), State.WAITING, 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void getAllRejectedBookingsByUserIdAndState() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(anyLong(),
                any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(firstBooking));

        List<Booking> bookings = bookingService.getAllByUserIdAndState(user.getId(), State.REJECTED, 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void getAllBookingsOfUserItems_WithoutItems() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findAllItemIdsByUserId(anyLong()))
                .thenReturn(Collections.emptyList());

        List<Booking> bookings = bookingService.getAllBookingsOfUsersItemsByUserId(1L, State.ALL, 0, 10);

        assertTrue(bookings.isEmpty());
    }

    @Test
    void getAllCurrentBookingsOfUserItemsByUserId() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findAllItemIdsByUserId(anyLong()))
                .thenReturn(List.of(item.getId()));
        when(bookingRepository.findAllCurrentByItemIds(anyList(), any(Pageable.class)))
                .thenReturn(List.of(firstBooking));

        List<Booking> bookings = bookingService.getAllBookingsOfUsersItemsByUserId(1L, State.CURRENT, 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void getAllPastBookingsOfUserItemsByUserId() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findAllItemIdsByUserId(anyLong()))
                .thenReturn(List.of(item.getId()));
        when(bookingRepository.findAllPreviousByItemIds(anyList(), any(Pageable.class)))
                .thenReturn(List.of(firstBooking));

        List<Booking> bookings = bookingService.getAllBookingsOfUsersItemsByUserId(1L, State.PAST, 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void getAllFutureBookingsOfUserItemsByUserId() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findAllItemIdsByUserId(anyLong()))
                .thenReturn(List.of(item.getId()));
        when(bookingRepository.findAllUpcomingByItemIds(anyList(), any(Pageable.class)))
                .thenReturn(List.of(firstBooking));

        List<Booking> bookings = bookingService.getAllBookingsOfUsersItemsByUserId(1L, State.FUTURE,
                0, 10);

        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void getAllWaitingBookingsOfUserItemsByUserId() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findAllItemIdsByUserId(anyLong()))
                .thenReturn(List.of(item.getId()));
        when(bookingRepository.findAllByItem_IdInAndStatusOrderByStartDesc(anyList(),
                any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(firstBooking));

        List<Booking> bookings = bookingService.getAllBookingsOfUsersItemsByUserId(1L, State.WAITING, 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void getAllRejectedBookingsOfUserItemsByUserId() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findAllItemIdsByUserId(anyLong()))
                .thenReturn(List.of(item.getId()));
        when(bookingRepository.findAllByItem_IdInAndStatusOrderByStartDesc(anyList(),
                any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(firstBooking));

        List<Booking> bookings = bookingService.getAllBookingsOfUsersItemsByUserId(1L, State.REJECTED, 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void getBookingById_WhenBookingNotFound() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
                bookingService.getById(1L, 1L));
    }

    @Test
    void getBookingById_WhenItIsNotOwner() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(secondBooking));
        assertThrows(NotFoundException.class, () ->
                bookingService.getById(1L, 2L));
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
