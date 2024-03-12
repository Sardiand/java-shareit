package ru.practicum.shareit.jpa;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    static UserDto userDto;

    @BeforeAll
    static void setUserDto() {
        userDto = new UserDto("User1", "user1@user.com");
    }

    @BeforeEach
    void cleanRepository() {
        userRepository.deleteAll();
    }

    @Test
    void createUser() {
        User user = userRepository.save(UserMapper.fromUserDto(userDto));

        assertEquals(1, userRepository.findAll().size());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void createUserWithSameEmail() {
        userRepository.save(UserMapper.fromUserDto(userDto));

        assertThrows(DataIntegrityViolationException.class, () ->
                userRepository.save(UserMapper.fromUserDto(userDto)));
    }

    @Test
    void getUserById() {
        User user = userRepository.save(UserMapper.fromUserDto(userDto));
        User receivedUser = userRepository.findById(user.getId()).orElseThrow();

        assertEquals(user.getId(), receivedUser.getId());
        assertEquals(userDto.getName(), receivedUser.getName());
        assertEquals(userDto.getEmail(), receivedUser.getEmail());
    }

    @Test
    void updateUser() {
        User user = userRepository.save(UserMapper.fromUserDto(userDto));
        user.setName("updatedUser");
        User updatedUser = userRepository.save(user);

        assertEquals(user.getId(), updatedUser.getId());
        assertEquals("updatedUser", updatedUser.getName());
    }
}
