package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.util.UtilityStuff.logError;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Booking create(long userId, BookingDto bookingDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> logError(new NotFoundException("Пользователь с id "
                        + userId + " не найден.")));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> logError(new NotFoundException("Вещь с id "
                        + bookingDto.getItemId() + " не найдена.")));
        if (userId == item.getOwner().getId()) {
            throw logError(new NotFoundException("Бронирование вещи её владельцем невозможно."));
        }
        if (!item.getAvailable()) {
            throw logError(new BadRequestException("Бронирование вещи с id "
                    + item.getId() + " недоступно"));
        }
        Booking booking = BookingMapper.toBooking(bookingDto, user, item);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking getById(long userId, long bookingId) {
        throwExceptionIfUserNotExist(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> logError(new NotFoundException("Бронирование с id "
                        + bookingId + " не найдено.")));
        if (userId != booking.getBooker().getId() && userId != booking.getItem().getOwner().getId()) {
            throw logError(new NotFoundException("У пользователя с id " + userId
                    + " нет доступа к бронированию с id " + bookingId + "."));
        }

        return booking;
    }

    @Override
    public Booking update(long userId, long bookingId, boolean available) {
        throwExceptionIfUserNotExist(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> logError(new NotFoundException("Бронирование с id "
                        + bookingId + " не найдено.")));
        if (userId != booking.getItem().getOwner().getId()) {
            throw logError(new NotFoundException("У пользователя с id " + userId
                    + " нет доступа к бронированию с id " + bookingId + "."));
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw logError(new BadRequestException("Бронирование с id " + bookingId
                    + " уже находится в статусе " + booking.getStatus() + "."));
        }
        booking.setStatus(available ? Status.APPROVED : Status.REJECTED);

        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getAllByUserIdAndState(long userId, State state, int from, int size) {
        throwExceptionIfUserNotExist(userId);
        List<Booking> bookings = new ArrayList<>();
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("start").descending());

        switch (state) {
            case ALL:
                bookings = new ArrayList<>(bookingRepository.findAllByBooker_IdOrderByStartDesc(userId, pageable));
                break;
            case CURRENT:
                bookings = new ArrayList<>(bookingRepository
                        .findAllCurrentByBookerId(userId, pageable));
                break;
            case PAST:
                bookings = new ArrayList<>(bookingRepository
                        .findAllPreviousByBookerId(userId, pageable));
                break;
            case FUTURE:
                bookings = new ArrayList<>(bookingRepository
                        .findAllUpcomingByBookerId(userId, pageable));
                break;
            case WAITING:
                bookings = new ArrayList<>(bookingRepository
                        .findAllByBooker_IdAndStatusOrderByStartDesc(userId, Status.WAITING, pageable));
                break;
            case REJECTED:
                bookings = new ArrayList<>(bookingRepository
                        .findAllByBooker_IdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageable));
                break;
        }
        return bookings;
    }

    @Override
    public List<Booking> getAllBookingsOfUsersItemsByUserId(long userId, State state, int from, int size) {
        throwExceptionIfUserNotExist(userId);
        List<Long> itemIds = new ArrayList<>(itemRepository.findAllItemIdsByUserId(userId));
        List<Booking> bookings = new ArrayList<>();
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("start").descending());
        if (itemIds.isEmpty()) {
            return new ArrayList<>();
        }
        switch (state) {
            case ALL:
                bookings = new ArrayList<>(bookingRepository.findAllByItem_IdInOrderByStartDesc(itemIds, pageable));
                break;
            case CURRENT:
                bookings = new ArrayList<>(bookingRepository
                        .findAllCurrentByItemIds(itemIds, pageable));
                break;
            case PAST:
                bookings = new ArrayList<>(bookingRepository
                        .findAllPreviousByItemIds(itemIds, pageable));
                break;
            case FUTURE:
                bookings = new ArrayList<>(bookingRepository
                        .findAllUpcomingByItemIds(itemIds, pageable));
                break;
            case WAITING:
                bookings = new ArrayList<>(bookingRepository
                        .findAllByItem_IdInAndStatusOrderByStartDesc(itemIds, Status.WAITING, pageable));
                break;
            case REJECTED:
                bookings = new ArrayList<>(bookingRepository
                        .findAllByItem_IdInAndStatusOrderByStartDesc(itemIds, Status.REJECTED, pageable));
                break;
        }
        return bookings;
    }

    private void throwExceptionIfUserNotExist(long userId) {
        if (!userRepository.existsById(userId)) {
            throw logError(new NotFoundException("Пользователь с id " + userId + " не найден."));
        }
    }
}
