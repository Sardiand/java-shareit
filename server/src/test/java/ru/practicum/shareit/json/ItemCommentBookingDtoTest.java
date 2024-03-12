package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentBookingDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemCommentBookingDtoTest {

    @Autowired
    private JacksonTester<ItemCommentBookingDto> jsonItemDto;

    private final BookingItemDto lastBooking = new BookingItemDto(1L, 1L);
    private final BookingItemDto nextBooking = new BookingItemDto(2L, 2L);
    private final CommentDto commentDto = new CommentDto(1L, "Comment", LocalDateTime.now().minusDays(1L),
            "Author");
    private final ItemCommentBookingDto item = new ItemCommentBookingDto(1L, "Item", "Descreption",
            true, lastBooking, nextBooking);


    @Test
    void itemCommentBookingDtoTest() throws IOException {
        item.getComments().add(commentDto);

        JsonContent<ItemCommentBookingDto> result = jsonItemDto.write(item);

        assertThat(result).extractingJsonPathNumberValue("$.id").satisfies(
                (number -> assertThat(number.longValue()).isEqualTo(item.getId())));
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(item.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(item.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(item.getAvailable());
        assertThat(result).extractingJsonPathNumberValue(
                "$.lastBooking.id").satisfies((number ->
                assertThat(number.longValue()).isEqualTo(item.getLastBooking().getId())));
        assertThat(result).extractingJsonPathNumberValue(
                "$.nextBooking.id").satisfies((number ->
                assertThat(number.longValue()).isEqualTo(item.getNextBooking().getId())));
        assertThat(result).extractingJsonPathStringValue(
                "$.comments.[0].text").isEqualTo(item.getComments().get(0).getText());
        assertThat(result).extractingJsonPathStringValue(
                "$.comments.[0].created").isEqualTo(item.getComments().get(0).getCreated().format(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
