package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.IncomingItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    @Autowired
    private final ItemRequestService itemRequestServiceImpl;

    @PostMapping
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Valid @RequestBody IncomingItemRequestDto dto) {
        return itemRequestServiceImpl.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return itemRequestServiceImpl.getAllByRequesterId(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable long requestId) {
        return itemRequestServiceImpl.getById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                       @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return itemRequestServiceImpl.getAll(userId, from, size);
    }
}
