package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    @Autowired
    private final UserService userServiceImpl;

    @PostMapping
    public User addNewUser(@Valid @RequestBody UserDto userDto) {
        if (userDto == null) {
            BadRequestException exp = new BadRequestException("User не может быть null");
            log.error("Ошибка: " + exp.getMessage());
            throw exp;
        }
        return userServiceImpl.createUser(userDto);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable("id") long userId) {
        return userServiceImpl.getUserById(userId);
    }

    @GetMapping
    public List<User> getAll() {
        return userServiceImpl.getAllUsers();
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable("id") long userId, @Valid @RequestBody UserDto userDto) {
        if (userDto == null) {
            BadRequestException exp = new BadRequestException("User не может быть null");
            log.error("Ошибка: " + exp.getMessage());
            throw exp;
        }
        return userServiceImpl.updateUser(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long userId) {
        userServiceImpl.deleteUserById(userId);
    }
}
