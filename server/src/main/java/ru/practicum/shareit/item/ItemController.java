package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemCommentBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemController {
    @Autowired
    private final ItemService itemServiceImpl;

    @PostMapping
    public ItemDto addNewItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        return itemServiceImpl.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto changeItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Valid @RequestBody ItemDto itemDto, @PathVariable("id") long itemId) {

        return itemServiceImpl.update(userId, itemId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemCommentBookingDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable("id") long itemId) {
        return itemServiceImpl.getById(userId, itemId);
    }

    @GetMapping
    public List<ItemCommentBookingDto> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                         @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return itemServiceImpl.getAllUsersItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<Item> getAllByTextRequest(@RequestParam(defaultValue = " ") String text,
                                          @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                          @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return itemServiceImpl.getAllByTextRequest(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId,
                                 @Valid @RequestBody IncomingCommentDto commentDto) {
        return itemServiceImpl.createCommentToItem(userId, itemId, commentDto);
    }
}
