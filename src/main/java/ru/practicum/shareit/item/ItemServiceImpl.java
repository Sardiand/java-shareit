package ru.practicum.shareit.item;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentItemDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemCommentBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.item.dto.ItemMapper.*;
import static ru.practicum.shareit.util.UtilityStuff.logError;

@RequiredArgsConstructor
@Service
@Data
@Validated
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        User user = userRepository.findById(userId).orElseThrow(() ->
                logError(new NotFoundException("Пользователь с id равным " + userId + " не найден.")));
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    logError(new NotFoundException("Запрос с id равным " + itemDto.getRequestId() + " не найден.")));
            item.setRequest(request);
        }

        return toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                logError(new NotFoundException("Предмет с id равным " + itemId + " не найден.")));
        userRepository.findById(userId).orElseThrow(() ->
                logError(new NotFoundException("Пользователь с id равным " + userId + " не найден.")));
        if (item.getOwner().getId() != userId) {
            throw logError(new ForbiddenException("Изменение полей предмета доступно только опубликовавшему " +
                    "его пользователю."));
        }
        Item updatedItem = ItemMapper.changeItemByDTO(item, itemDto);

        return toItemDto(itemRepository.save(updatedItem));
    }

    @Override
    public ItemCommentBookingDto getById(long userId, long itemId) {
        if (!userRepository.existsById(userId)) {
            throw logError(new NotFoundException("Пользователь с id равным " + userId + " не найден."));
        }

        ItemBookingDto item = itemRepository.findItemBookingDtoById(itemId)
                .orElseThrow(() -> logError(new NotFoundException("Предмет с id равным " + itemId + " не найден.")));
        List<CommentDto> comments = commentRepository.findAllCommentDtoByItemId(itemId);

        return toItemCommentBookingDto(userId, item, comments);
    }

    @Override
    public List<ItemCommentBookingDto> getAllUsersItems(long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() ->
                logError(new NotFoundException("Пользователь с id равным " + userId + " не найден.")));
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").ascending());
        List<Long> itemIds = itemRepository.findAllItemIdsByUserId(userId);
        if (itemIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<CommentItemDto> comments = commentRepository.findAllCommentDtoByItemIds(itemIds);
        List<ItemBookingDto> items = itemRepository.findAllItemBookingDto(userId, pageable);

        return toItemCommentBookingDtos(userId, items, comments);
    }

    @Override
    public List<Item> getAllByTextRequest(String request, int from, int size) {
        if (request.isBlank()) {
            return new ArrayList<>();
        }
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").ascending());
        return itemRepository.findAllByTextRequest(request, pageable);
    }

    @Override
    public CommentDto createCommentToItem(long userId, long itemId, IncomingCommentDto incomingCommentDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                logError(new NotFoundException("Пользователь с id равным " + userId + " не найден.")));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                logError(new NotFoundException("Предмет с id равным " + itemId + " не найден.")));
        if (bookingRepository.findPreviousByItemIdAndUserId(itemId, userId, Status.APPROVED)
                .isEmpty()) {
            throw new BadRequestException(String.format("Пользователь с id %d не арендовал предмет с id %d, " +
                    "в связи с чем не может оставлять комментарии к предмету.", userId, itemId));
        }
        Comment comment = commentRepository.save(CommentMapper.fromIncomingDto(user, item, incomingCommentDto));

        return CommentMapper.toCommentDto(comment);
    }
}
