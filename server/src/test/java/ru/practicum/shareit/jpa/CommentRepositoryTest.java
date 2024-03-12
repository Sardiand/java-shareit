package ru.practicum.shareit.jpa;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    User user;
    Item item;
    User commentator;
    Comment comment;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.AAAAA");


    @BeforeEach
    void setUp() {
        user = new User("testUser", "user@user.com");
        userRepository.save(user);
        commentator = new User("commentator", "commentator@user.com");
        userRepository.save(commentator);

        item = new Item("Грааль", "Святой", true, null);
        item.setOwner(user);
        item = itemRepository.save(item);

        comment = new Comment("testComment", LocalDateTime.now(), item, commentator);
        commentRepository.save(comment);

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
    }

    @AfterEach
    void cleanAll() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getAllCommentItemDto() {
        List<CommentItemDto> dtos = commentRepository.findAllCommentDtoByItemIds(List.of(item.getId()));

        assertEquals(1, dtos.size());
        assertEquals(comment.getId(), dtos.get(0).getId());
        assertEquals(comment.getText(), dtos.get(0).getText());
        assertEquals(comment.getCreated().format(formatter),
                dtos.get(0).getCreated().format(formatter));
        assertEquals(comment.getAuthor().getName(), dtos.get(0).getAuthorName());
        assertEquals(comment.getItem().getId(), dtos.get(0).getItemId());
    }

    @Test
    void getAllCommentDtoByItemId() {
        List<CommentDto> dtos = commentRepository.findAllCommentDtoByItemId(item.getId());

        assertEquals(1, dtos.size());
        assertEquals(comment.getId(), dtos.get(0).getId());
        assertEquals(comment.getText(), dtos.get(0).getText());
        assertEquals(comment.getCreated().format(formatter),
                dtos.get(0).getCreated().format(formatter));
        assertEquals(comment.getAuthor().getName(), dtos.get(0).getAuthorName());
    }
}
