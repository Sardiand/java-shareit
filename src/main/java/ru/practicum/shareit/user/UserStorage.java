package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStorage {

    User add(User user);

    Optional<User> findById(long userId);

    List<User> findAll();

    User edit(User user);

    void deleteById(long userId);

    boolean checkIsEmailExist(UserDto userDto);
}
