package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserUnitTest {

    @InjectMocks
    UserServiceImpl userServiceImpl;
    @Mock
    UserRepository userRepository;
    UserDto userDto;
    User user;

    @BeforeEach
    void setUserServiceImpl() {
        userServiceImpl = new UserServiceImpl(userRepository);
        userDto = new UserDto("User1", "user1@user.com");
        user = UserMapper.fromUserDto(userDto);
        user.setId(1L);
    }

    @Test
    void createUserWithoutName() {
        UserDto userNoName = new UserDto(null, "noname@user.com");

        assertThrows(IllegalArgumentException.class, () ->
                userServiceImpl.create(userNoName));
    }

    @Test
    void createUserWithoutEmail() {
        UserDto userNoEmail = new UserDto("NoEmail", null);

        assertThrows(IllegalArgumentException.class, () ->
                userServiceImpl.create(userNoEmail));
    }

    @Test
    void getUserByIdWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                userServiceImpl.getById(99L));
    }

    @Test
    void updateWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                userServiceImpl.update(99L, userDto));
    }

    @Test
    void deleteByIdWhenUserNotFound() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                userServiceImpl.deleteById(99L));
    }
}
