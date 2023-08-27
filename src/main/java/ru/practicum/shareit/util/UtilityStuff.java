package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class UtilityStuff {

    public static RuntimeException logError(RuntimeException exp) {
        log.error("Ошибка: " + exp.getMessage());
        return exp;
    }
}
