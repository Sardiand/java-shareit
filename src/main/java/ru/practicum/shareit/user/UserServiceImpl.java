package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserStorage userStorageImpl;
    @Autowired
    private final ItemStorage inMemoryItemStorage;

    private long id = 0;

    @Override
    public User createUser(UserDto userDto) {
        if (userStorageImpl.checkIsEmailExist(userDto)) {
            ConflictException exp = new ConflictException("Пользователь с указанным адресом электронной почты уже существует.");
            log.error("Ошибка: " + exp.getMessage());
            throw exp;
        }
        User user = UserMapper.fromUserDto(userDto);
        id++;
        user.setId(id);
        return userStorageImpl.add(user);
    }

    @Override
    public User getUserById(long userId) {
        if (userStorageImpl.findById(userId) == null) {
            BadRequestException exp = new BadRequestException("User с id " + userId + " не найден.");
            log.error("Ошибка: " + exp.getMessage());
            throw exp;
        }
        return userStorageImpl.findById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userStorageImpl.findAll();
    }

    @Override
    public User updateUser(long userId, UserDto userDto) {
        if (userStorageImpl.findById(userId) == null) {
            BadRequestException exp = new BadRequestException("User не найден.");
            log.error("Ошибка: " + exp.getMessage());
            throw exp;
        }
        User user = userStorageImpl.findById(userId);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (userStorageImpl.checkIsEmailExist(userDto) &&
                    !userDto.getEmail().equals(userStorageImpl.findById(userId).getEmail())) {
                ConflictException exp = new ConflictException("Пользователь с указанным адресом электронной почты" +
                        " уже существует.");
                log.error("Ошибка: " + exp.getMessage());
                throw exp;
            }
            user.setEmail(userDto.getEmail());
        }
        return userStorageImpl.edit(user);
    }

    @Override
    public void deleteUserById(long userId) {
        userStorageImpl.deleteById(userId);
        inMemoryItemStorage.deleteAllByUserId(userId);
    }
}
