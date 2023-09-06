package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemCommentBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    @Autowired
    private final ItemService itemServiceImpl;

    @PostMapping
    public Item addNewItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        if (itemDto == null) {
            BadRequestException e = new BadRequestException("Передаваемый itemDto не может быть null.");
            log.error("Ошибка: " + e.getMessage());
            throw e;
        }
        return itemServiceImpl.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public Item changeItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @Valid @RequestBody ItemDto itemDto, @PathVariable("id") long itemId) {
        if (itemDto == null) {
            BadRequestException e = new BadRequestException("Передаваемый itemDto не может быть null.");
            log.error("Ошибка: " + e.getMessage());
            throw e;
        }
        return itemServiceImpl.update(userId, itemId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemCommentBookingDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable("id") long itemId) {
        return itemServiceImpl.getById(userId, itemId);
    }

    @GetMapping
    public List<ItemCommentBookingDto> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemServiceImpl.getAllUsersItems(userId);
    }

    @GetMapping("/search")
    public List<Item> getAllByTextRequest(@RequestParam(defaultValue = " ") String text) {
        return itemServiceImpl.getAllByTextRequest(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId,
                                 @Valid @RequestBody IncomingCommentDto commentDto) {
        return itemServiceImpl.createCommentToItem(userId, itemId, commentDto);
    }
}
