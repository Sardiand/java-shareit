package ru.practicum.shareit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.IncomingItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    ItemRequestService requestService;

    private final IncomingItemRequestDto itemRequestDto = new IncomingItemRequestDto("Чаша");
    private final ItemRequestDto outRequestDto = new ItemRequestDto(1L, "Чаша", LocalDateTime.now());

    @Test
    @SneakyThrows
    void addRequest() {
        doReturn(outRequestDto)
                .when(requestService)
                .create(anyLong(), any(IncomingItemRequestDto.class));

        mvc.perform(post("/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description",
                        is(outRequestDto.getDescription())))
                .andExpect(jsonPath("$.created",
                        is(outRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));

        IncomingItemRequestDto invalidDto = new IncomingItemRequestDto("");

        mvc.perform(post("/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getRequests() {
        ArrayList<ItemRequestDto> list = new ArrayList<>();
        list.add(outRequestDto);
        doReturn(list)
                .when(requestService)
                .getAll(anyLong(), anyInt(), anyInt());

        mvc.perform(get("/requests")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getRequestById() {
        doReturn(outRequestDto)
                .when(requestService)
                .getById(anyLong(), anyLong());

        mvc.perform(get("/requests/{requestId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description",
                        is(outRequestDto.getDescription())))
                .andExpect(jsonPath("$.created",
                        is(outRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    @SneakyThrows
    void getAllRequestsByRequesterId() {
        doReturn(List.of(outRequestDto))
                .when(requestService)
                .getAllByRequesterId(anyLong(), anyInt(), anyInt());

        mvc.perform(get("/requests/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }
}
