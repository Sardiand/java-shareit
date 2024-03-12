package ru.practicum.shareit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private final BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2));
    private final BookingDto invalidBookingDto = new BookingDto(1L, LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(2));

    private static User owner;
    private static User booker;
    private static Item item;

    private static Booking booking;

    @BeforeAll
    static void setUp() {
        owner = new User("testOwner", "owner@user.com");
        owner.setId(1L);
        booker = new User("testBooker", "booker@user.com");
        booker.setId(2L);
        item = new Item("Грааль", "Святой", true, null);
        booking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, Status.WAITING);
    }

    @Test
    @SneakyThrows
    void addBooking() {
        doReturn(booking)
                .when(bookingService)
                .create(anyLong(), any(BookingDto.class));

        mvc.perform(post("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status",
                        is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id",
                        is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id",
                        is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name",
                        is(booking.getItem().getName())));

        mvc.perform(post("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(invalidBookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void addBooking_whenFailed() {
        doThrow(BadRequestException.class)
                .when(bookingService)
                .create(anyLong(), any(BookingDto.class));

        mvc.perform(post("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());

        doThrow(BadRequestException.class)
                .when(bookingService)
                .create(anyLong(), any(BookingDto.class));

        mvc.perform(post("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateBooking() {
        booking.setStatus(Status.APPROVED);
        doReturn(booking)
                .when(bookingService)
                .update(anyLong(), anyLong(), anyBoolean());

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status",
                        is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id",
                        is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id",
                        is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name",
                        is(booking.getItem().getName())));
    }

    @Test
    @SneakyThrows
    void updateBooking_whenFailed() {
        doThrow(IllegalArgumentException.class)
                .when(bookingService)
                .update(anyLong(), anyLong(), anyBoolean());

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        doThrow(NotFoundException.class)
                .when(bookingService)
                .update(anyLong(), anyLong(), anyBoolean());

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getBooking() {
        doReturn(booking)
                .when(bookingService)
                .getById(anyLong(), anyLong());

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status",
                        is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id",
                        is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id",
                        is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name",
                        is(booking.getItem().getName())));
    }

    @Test
    @SneakyThrows
    void getBooking_whenFailed() {
        doThrow(NotFoundException.class)
                .when(bookingService)
                .getById(anyLong(), anyLong());

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());

        doThrow(NotFoundException.class)
                .when(bookingService)
                .getById(anyLong(), anyLong());

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());

        doThrow(NotFoundException.class)
                .when(bookingService)
                .getById(anyLong(), anyLong());

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getBookings() {
        doReturn(List.of(booking))
                .when(bookingService)
                .getAllByUserIdAndState(anyLong(), any(State.class), anyInt(), anyInt());

        mvc.perform(get("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start",
                        is(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end",
                        is(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].status",
                        is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.[0].booker.id",
                        is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.id",
                        is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name",
                        is(booking.getItem().getName())));
    }

    @Test
    @SneakyThrows
    void getBookings_whenFailed() {
        doThrow(MethodArgumentTypeMismatchException.class)
                .when(bookingService)
                .getAllByUserIdAndState(anyLong(), any(State.class), anyInt(), anyInt());

        String error = mvc.perform(get("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "Bad"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertEquals("{\"error\":\"Unknown state: Bad\"}", error);
    }

    @Test
    @SneakyThrows
    void getBookingsForOwnedItems_whenFailed() {
        doThrow(MethodArgumentTypeMismatchException.class)
                .when(bookingService)
                .getAllBookingsOfUsersItemsByUserId(anyLong(), any(State.class), anyInt(), anyInt());

        String error = mvc.perform(get("/bookings/owner")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "Bad"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertEquals("{\"error\":\"Unknown state: Bad\"}", error);
    }

    @Test
    @SneakyThrows
    void getBookingsForOwnedItems() {
        doReturn(List.of(booking))
                .when(bookingService)
                .getAllBookingsOfUsersItemsByUserId(anyLong(), any(State.class), anyInt(), anyInt());

        mvc.perform(get("/bookings/owner")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start",
                        is(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end",
                        is(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].status",
                        is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.[0].booker.id",
                        is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.id",
                        is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name",
                        is(booking.getItem().getName())));
    }
}
