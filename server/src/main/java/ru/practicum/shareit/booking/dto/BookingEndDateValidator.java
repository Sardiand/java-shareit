package ru.practicum.shareit.booking.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingEndDateValidator implements ConstraintValidator<OnOrBeforeBookingStartDate, BookingDto> {
    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        return bookingDto.getEnd().isAfter(bookingDto.getStart());
    }
}
