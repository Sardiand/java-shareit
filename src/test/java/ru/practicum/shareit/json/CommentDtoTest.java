package ru.practicum.shareit.json;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> jsonItemDto;

    @Test
    void testCommentDto() throws IOException {
        CommentDto commentDto = new CommentDto(1L, "Comment", LocalDateTime.now(), "Author");

        JsonContent<CommentDto> json = jsonItemDto.write(commentDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").satisfies(
                (number -> assertThat(number.longValue()).isEqualTo(commentDto.getId())));
        assertThat(json).extractingJsonPathStringValue(
                "$.text").isEqualTo(commentDto.getText());
        assertThat(json).extractingJsonPathStringValue(
                "$.created").isEqualTo(commentDto.getCreated().format(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathStringValue("$.authorName").isEqualTo(commentDto.getAuthorName());
    }
}
