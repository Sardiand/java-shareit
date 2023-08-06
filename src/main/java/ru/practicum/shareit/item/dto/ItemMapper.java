package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.RequestStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ItemMapper {
    @Autowired
    private final RequestStorage inMemoryRequestStorage;
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    Validator validator = validatorFactory.getValidator();

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public Item toItem(ItemDto itemDto) {
        Item item = new Item(itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                itemDto.getRequest() != null ? inMemoryRequestStorage.findById(itemDto.getRequest()) : null);
        validateItem(item);
        return item;
    }

    public Item changeItemByDTO(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getRequest() != null) {
            item.setRequest(inMemoryRequestStorage.findById(itemDto.getRequest()));
        }
        validateItem(item);
        return item;
    }

    private void validateItem(Item item) {
        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        if (!violations.isEmpty()) {
            String messages = violations.stream().map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new BadRequestException(messages);
        }
    }
}
