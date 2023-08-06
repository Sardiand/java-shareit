package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Repository
public interface UserStorage {

    User add(User user);

    User findById(long userId);

    List<User> findAll();

    User edit(User user);

    void deleteById(long userId);

    boolean checkIsEmailExist(UserDto userDto);
}
