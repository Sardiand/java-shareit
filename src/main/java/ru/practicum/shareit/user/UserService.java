package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {

    User createUser(UserDto userDto);

    User getUserById(long userId);

    List<User> getAllUsers();

    User updateUser(long userId, UserDto userDto);

    void deleteUserById(long userId);
}
