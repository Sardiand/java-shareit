package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {

    User create(UserDto userDto);

    User getById(long userId);

    List<User> getAll();

    User update(long userId, UserDto userDto);

    void deleteById(long userId);
}
