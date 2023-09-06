package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.Item;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class UtilityStuff {

    public static RuntimeException logError(RuntimeException exp) {
        log.error("Ошибка: " + exp.getMessage());
        return exp;
    }

    public static void validateItem(Item item) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        if (!violations.isEmpty()) {
            String messages = violations.stream().map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new BadRequestException(messages);
        }
    }
}
