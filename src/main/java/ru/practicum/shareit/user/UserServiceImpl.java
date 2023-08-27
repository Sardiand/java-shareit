package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.util.UtilityStuff;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ItemStorage inMemoryItemStorage;

    @Override
    public User create(UserDto userDto) {
        return userRepository.save(UserMapper.fromUserDto(userDto));
    }

    @Override
    public User getById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                UtilityStuff.logError(new NotFoundException("User с id " + userId + " не найден.")));
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                UtilityStuff.logError(new NotFoundException("User с id " + userId + " не найден.")));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteById(long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            inMemoryItemStorage.deleteAllByUserId(userId);
        } else {
            throw UtilityStuff.logError(new NotFoundException("User с id " + userId + " не найден."));
        }

    }
}
