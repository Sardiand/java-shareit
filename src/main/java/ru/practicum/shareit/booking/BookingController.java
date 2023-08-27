package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.util.UtilityStuff;

import javax.validation.Valid;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking addNewBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking updateBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId,
                                 @RequestParam boolean approved) {
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestParam(defaultValue = "ALL") String state) {
        try {
            State convertedState = State.valueOf(state);
            return bookingService.getAllByUserIdAndState(userId, convertedState);
        } catch (IllegalArgumentException exp) {
            throw UtilityStuff.logError(new BadRequestException("Unknown state: " + state));
        }
    }

    @GetMapping("/owner")
    public List<Booking> getBookingsForOwnedItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        try {
            State convertedState = State.valueOf(state);
            return bookingService.getAllBookingsOfUsersItemsByUserId(userId, convertedState);
        } catch (IllegalArgumentException exp) {
            throw UtilityStuff.logError(new BadRequestException("Unknown state: " + state));
        }
    }
}
