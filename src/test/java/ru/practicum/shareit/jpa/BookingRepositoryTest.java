package ru.practicum.shareit.jpa;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        owner = new User("testOwner", "owner@user.com");
        userRepository.save(owner);
        booker = new User("testBooker", "booker@user.com");
        userRepository.save(booker);

        item = new Item("Грааль", "Святой", true, null);
        item.setOwner(owner);
        itemRepository.save(item);

        booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);

        pageable = PageRequest.of(0, 10, Sort.by("start").descending());
    }

    @AfterEach
    void cleanAll() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllCurrent() {
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        bookingRepository.save(booking);

        List<Booking> currentBookings =
                bookingRepository.findAllCurrentByBookerId(booker.getId(), pageable);

        assertEquals(1, currentBookings.size());
        assertEquals(booking.getStart(), currentBookings.get(0).getStart());
        assertEquals(booking.getEnd(), currentBookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), currentBookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), currentBookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), currentBookings.get(0).getStatus());
    }

    @Test
    void findAllPast() {
        booking.setStart(LocalDateTime.now().minusHours(3));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking);

        List<Booking> pastBookings =
                bookingRepository.findAllPreviousByBookerId(booker.getId(), pageable);

        assertEquals(1, pastBookings.size());
        assertEquals(booking.getStart(), pastBookings.get(0).getStart());
        assertEquals(booking.getEnd(), pastBookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), pastBookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), pastBookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), pastBookings.get(0).getStatus());
    }

    @Test
    void findAllFuture() {
        booking.setStart(LocalDateTime.now().plusHours(2));
        booking.setEnd(LocalDateTime.now().plusHours(3));
        bookingRepository.save(booking);

        List<Booking> futureBookings =
                bookingRepository.findAllUpcomingByBookerId(booker.getId(), pageable);

        assertEquals(1, futureBookings.size());
        assertEquals(booking.getStart(), futureBookings.get(0).getStart());
        assertEquals(booking.getEnd(), futureBookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), futureBookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), futureBookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), futureBookings.get(0).getStatus());
    }

    @Test
    void findAllByItem_IdCurrent() {
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().plusHours(3));
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findAllCurrentByItemIds(List.of(item.getId()), pageable);

        assertEquals(1, bookings.size());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findLastBooking() {
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findPreviousByItemIdAndUserId(item.getId(), booker.getId(),
                Status.APPROVED);

        assertEquals(1, bookings.size());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findLastBookingList() {
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findAllPreviousByItemIds(List.of(item.getId()), pageable);

        assertEquals(1, bookings.size());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findNextBookingList() {
        booking.setStart(LocalDateTime.now().plusHours(2));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findAllUpcomingByItemIds(List.of(item.getId()), pageable);

        assertEquals(1, bookings.size());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }
}
