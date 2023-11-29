package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
        return userServiceImpl.create(userDto);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable("id") long userId) {
        return userServiceImpl.getById(userId);
    }

    @GetMapping
    public List<User> getAll() {
        return userServiceImpl.getAll();
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable("id") long userId, @Valid @RequestBody UserDto userDto) {
        return userServiceImpl.update(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long userId) {
        userServiceImpl.deleteById(userId);
    }
}
