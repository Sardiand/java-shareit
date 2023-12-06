package ru.practicum.shareit.rest;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemCommentBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;

    private final ItemDto itemDto = new ItemDto(1L, "Грааль", "Святой", true, 1L);
    private final ItemCommentBookingDto dto = new ItemCommentBookingDto(1L, "Грааль", "Святой+",
            true, new BookingItemDto(1L, 1L), new BookingItemDto(2L, 1L));
    private final CommentDto commentDto = new CommentDto(1L, "Божественно", LocalDateTime.now(),
            "Мишаня");

    @Test
    @SneakyThrows
    void createItem() {
        doReturn(itemDto)
                .when(itemService)
                .create(anyLong(), any(ItemDto.class));

        mvc.perform(post("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        itemDto.setName("");

        doThrow(IllegalArgumentException.class)
                .when(itemService)
                .create(1L, itemDto);

        mvc.perform(post("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void addComment() {
        IncomingCommentDto dto = new IncomingCommentDto();

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        dto.setText("comment");

        doReturn(commentDto)
                .when(itemService)
                .createCommentToItem(anyLong(), anyLong(), any(IncomingCommentDto.class));

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created",
                        is(commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));

        doThrow(IllegalArgumentException.class)
                .when(itemService)
                .createCommentToItem(anyLong(), anyLong(), any(IncomingCommentDto.class));

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateItem() {
        doThrow(new NotFoundException(""))
                .when(itemService)
                .update(anyLong(), anyLong(), any(ItemDto.class));
        mvc.perform(patch("/items/{itemId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());

        doReturn(itemDto)
                .when(itemService)
                .update(anyLong(), anyLong(), any(ItemDto.class));

        mvc.perform(patch("/items/{itemId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    void updateItemWithNull() {
        mvc.perform(patch("/items/{itemId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getItems() {
        doReturn(List.of(dto))
                .when(itemService)
                .getAllUsersItems(anyLong(), anyInt(), anyInt());

        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(dto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(dto.getAvailable())))
                .andExpect(jsonPath("$.[0].nextBooking.id",
                        is(dto.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.[0].nextBooking.bookerId",
                        is(dto.getNextBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.[0].lastBooking.id",
                        is(dto.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.[0].lastBooking.bookerId",
                        is(dto.getLastBooking().getBookerId()), Long.class));
    }

    @Test
    @SneakyThrows
    void getItemById() {
        doReturn(dto)
                .when(itemService)
                .getById(anyLong(), anyLong());

        mvc.perform(get("/items/{itemId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.available", is(dto.getAvailable())))
                .andExpect(jsonPath("$.nextBooking.id",
                        is(dto.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.bookerId",
                        is(dto.getNextBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.id",
                        is(dto.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.bookerId",
                        is(dto.getLastBooking().getBookerId()), Long.class));


        doThrow(NotFoundException.class)
                .when(itemService)
                .getById(anyLong(), anyLong());

        mvc.perform(get("/items/{itemId}", 5)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());

    }

    @Test
    @SneakyThrows
    void searchItems() {
        doReturn(List.of(itemDto))
                .when(itemService)
                .getAllByTextRequest(anyString(), anyInt(), anyInt());

        mvc.perform(get("/items/search")
                        .param("text", "name")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())));
    }
}
