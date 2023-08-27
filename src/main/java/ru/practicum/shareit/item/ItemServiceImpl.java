package ru.practicum.shareit.item;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemCommentBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.util.UtilityStuff;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Data
@Validated
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Item create(long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        User user = userRepository.findById(userId).orElseThrow(() ->
                UtilityStuff.logError(new NotFoundException("Пользователь с id равным " + userId + " не найден.")));
        item.setOwner(user);
        item.setRequest(itemDto.getRequest());
        return itemRepository.save(item);
    }

    @Override
    public Item update(long userId, long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                UtilityStuff.logError(new NotFoundException("Предмет с id равным " + itemId + " не найден.")));
        ;
        userRepository.findById(userId).orElseThrow(() ->
                UtilityStuff.logError(new NotFoundException("Пользователь с id равным " + userId + " не найден.")));
        if (item.getOwner().getId() != userId) {
            throw UtilityStuff.logError(new ForbiddenException("Изменение полей предмета доступно только опубликовавшему " +
                    "его пользователю."));
        }
        Item updatedItem = ItemMapper.changeItemByDTO(item, itemDto);
        return itemRepository.save(updatedItem);
    }

    @Override
    public ItemCommentBookingDto getById(long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                UtilityStuff.logError(new NotFoundException("Предмет с id равным " + itemId + " не найден.")));
        return makeItemCBDFromItem(item);
    }

    @Override
    public List<ItemCommentBookingDto> getAllUsersItems(long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                UtilityStuff.logError(new NotFoundException("Пользователь с id равным " + userId + " не найден.")));
        return itemRepository.findAllByOwner_id(userId)
                .stream().map(this::makeItemCBDFromItem)
                .sorted(Comparator.comparing(ItemCommentBookingDto::getId, Comparator.naturalOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getAllByTextRequest(String request) {
        if (request.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findAllByTextRequest(request).stream()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Comment createCommentToItem(long userId, long itemId, IncomingCommentDto incomingCommentDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                UtilityStuff.logError(new NotFoundException("Пользователь с id равным " + userId + " не найден.")));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                UtilityStuff.logError(new NotFoundException("Предмет с id равным " + itemId + " не найден.")));
        if (bookingRepository.findPreviousByItemIdAndUserId(itemId, userId, LocalDateTime.now(), Status.APPROVED)
                .isEmpty()) {
            throw new BadRequestException(String.format("Пользователь с id %d не арендовал предмет с id %d, " +
                    "в связи с чем не может оставлять комментарии к предмету.", userId, itemId));
        }
        return commentRepository.save(CommentMapper.fromIncomingDto(user, item, incomingCommentDto));
    }

    private ItemCommentBookingDto makeItemCBDFromItem(Item item) {
        List<CommentDto> commentsDto = commentRepository.findAllByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        ItemCommentBookingDto itemCBD = ItemMapper.toItemCommentBookingDto(item);
        List<Booking> bookings = bookingRepository.findPreviousByItem_id(item.getId(),
                Status.APPROVED, LocalDateTime.now());
        if (!bookings.isEmpty()) {
            itemCBD.setLastBooking(BookingMapper.toBookingItemDto(bookings.get(0)));
        }
        bookings = bookingRepository.findUpcomingByItem_Id(item.getId(),
                Status.APPROVED, LocalDateTime.now());
        if (!bookings.isEmpty()) {
            itemCBD.setNextBooking(BookingMapper.toBookingItemDto(bookings.get(0)));
        }
        itemCBD.getComments().addAll(commentsDto);
        return itemCBD;
    }
}
