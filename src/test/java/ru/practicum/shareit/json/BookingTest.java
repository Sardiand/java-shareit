package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingTest {
    @Autowired
    private JacksonTester<Booking> jsonBooking;

    private Booking booking;

    @Test
    void bookingTest() throws IOException {
        User owner = new User("testOwner", "owner@user.com");
        owner.setId(1L);
        User booker = new User("testBooker", "booker@user.com");
        booker.setId(2L);
        Item item = new Item("Грааль", "Святой", true, null);
        item.setId(1L);
        booking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, Status.WAITING);

        JsonContent<Booking> result = jsonBooking.write(booking);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .satisfies((number -> assertThat(number.longValue()).isEqualTo(booking.getId())));
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end").
                isEqualTo(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .satisfies((number -> assertThat(number.longValue()).isEqualTo(booking.getItem().getId())));
        assertThat(result).extractingJsonPathStringValue("$.item.name")
                .isEqualTo(booking.getItem().getName());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .satisfies((number -> assertThat(number.longValue()).isEqualTo(booking.getBooker().getId())));
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(booking.getStatus().toString());
    }
}
