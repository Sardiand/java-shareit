package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Service
public interface BookingService {

    Booking create(long userId, BookingDto bookingDto);

    Booking update(long userId, long bookingId, boolean available);

    Booking getById(long userId, long bookingId);

    List<Booking> getAllByUserIdAndState(long userId, State state, int from, int size);

    List<Booking> getAllBookingsOfUsersItemsByUserId(long userId, State state, int from, int size);
}
