package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@OnOrBeforeBookingStartDate
public class BookingDto {
    @NonNull
    private Long itemId;
    @NonNull
    @Future
    private LocalDateTime start;
    @NonNull
    private LocalDateTime end;
}
