package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
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
                                     @RequestParam(defaultValue = "ALL") State state,
                                     @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                     @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return bookingService.getAllByUserIdAndState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<Booking> getBookingsForOwnedItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "ALL") State state,
                                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                  @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return bookingService.getAllBookingsOfUsersItemsByUserId(userId, state, from, size);
    }
}
