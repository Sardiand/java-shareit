package ru.practicum.shareit.booking.dto;


import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.*;

@Inherited
@Constraint(validatedBy = BookingEndDateValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnOrBeforeBookingStartDate {
    String message() default "Field \"end\" cannot be earlier than field \"start\".";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


