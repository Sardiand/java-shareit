package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    JacksonTester<ItemRequestDto> jsonRequestDto;

    private final ItemDto itemDto = new ItemDto(1L, "Item", "Description", true, 1L);
    private final ItemRequestDto request = new ItemRequestDto(1L, "Description", LocalDateTime.now());

    @Test
    void testItemRequestDto() throws IOException {
        request.getItems().add(itemDto);

        JsonContent<ItemRequestDto> result = jsonRequestDto.write(request);
        assertThat(result).extractingJsonPathNumberValue("$.id").satisfies(
                (number -> assertThat(number.longValue()).isEqualTo(request.getId())));
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(request.getDescription());
        assertThat(result).extractingJsonPathStringValue(
                "$.created").isEqualTo(request.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathNumberValue(
                "$.items.[0].id").satisfies((number ->
                assertThat(number.longValue()).isEqualTo(itemDto.getId())));
    }
}
