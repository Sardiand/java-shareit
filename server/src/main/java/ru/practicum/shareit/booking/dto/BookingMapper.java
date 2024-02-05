package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto, User user, Item item) {
        return new Booking(bookingDto.getStart(), bookingDto.getEnd(), item, user, Status.WAITING);
    }

    public static BookingItemDto toBookingItemDto(Long id, Long bookerId) {
        if (id == null) {
            return null;
        }
        return new BookingItemDto(id, bookerId);
    }
}
