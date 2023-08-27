package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.InMemoryItemStorage;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.dto.UserDto;

import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;


@SpringBootTest
class ShareItTests {
  /*  private UserController userController;
    private ItemController itemController;
    private static Validator validator;

    @BeforeAll
    static void setValidator() {
        validator = buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    void makeControllers() {
        InMemoryItemStorage inMemoryItemStorage = new InMemoryItemStorage();
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        UserServiceImpl userService = new UserServiceImpl(inMemoryUserStorage, inMemoryItemStorage);
        ItemServiceImpl itemService = new ItemServiceImpl(inMemoryItemStorage, inMemoryUserStorage);
        userController = new UserController(userService);
        itemController = new ItemController(itemService);
        userController.addNewUser(new UserDto("TestName", "test@test.com"));
    }


    @Test
    void createUser() {
        User user = userController.get(1);

        assertEquals(1, user.getId());
        assertEquals("TestName", user.getName());
        assertEquals("test@test.com", user.getEmail());

        UserDto userDto = null;
        try {
            userController.addNewUser(userDto);
        } catch (BadRequestException e) {
            assertEquals("User не может быть null", e.getMessage());
        }

        userDto = new UserDto("TestName2", "test@test.com");
        try {
            userController.addNewUser(userDto);
        } catch (ConflictException exp) {
            assertEquals("Пользователь с адресом электронной почты test@test.com уже существует.",
                    exp.getMessage());
        }

        userDto.setEmail("test.com");
        Set<ConstraintViolation<UserDto>> validateEmail = validator.validate(userDto);

        assertEquals("Неверный формат адреса электронной почты.",
                new ArrayList<>(validateEmail).get(0).getMessage());
    }

    @Test
    void updateUser() {
        userController.update(1, new UserDto("UpdateName", "update@update.com"));

        assertEquals("UpdateName", userController.get(1).getName());
        assertEquals("update@update.com", userController.get(1).getEmail());

        userController.addNewUser(new UserDto("TestName", "test@test.com"));
        try {
            userController.update(2, new UserDto("TestName", "update@update.com"));
        } catch (ConflictException exp) {
            assertEquals("Пользователь с адресом электронной почты update@update.com уже существует.",
                    exp.getMessage());
        }
    }

    @Test
    void getUser() {
        User user = userController.get(1);

        assertEquals("TestName", user.getName());
        assertEquals("test@test.com", user.getEmail());
    }

    @Test
    void getAllUsers() {
        userController.addNewUser(new UserDto("Second", "second@test.com"));
        List<User> users = userController.getAll();

        assertFalse(users.isEmpty());
        assertEquals(2, users.size());
        assertEquals("TestName", users.get(0).getName());
        assertEquals("second@test.com", users.get(1).getEmail());
    }

    @Test
    void deleteUser() {
        User user = userController.get(1);
        ;
        assertEquals("TestName", user.getName());

        userController.delete(1);
        try {
            userController.get(1);
        } catch (BadRequestException exp) {
            assertEquals("User с id 1 не найден.", exp.getMessage());
        }
    }

    @Test
    void createItem() {
        ItemDto itemDto = new ItemDto("Test", "TestDescription", true, null);
        Item item = itemController.addNewItem(1, itemDto);

        assertEquals(1, item.getId());
        assertEquals(1, item.getOwnerId());
        assertEquals("Test", item.getName());
        assertEquals("TestDescription", item.getDescription());
        assertTrue(item.getAvailable());

        try {
            itemController.addNewItem(2, itemDto);
        } catch (NotFoundException exp) {
            assertEquals("Пользователь с id равным 2 не найден.", exp.getMessage());
        }

        itemDto = null;
        try {
            itemController.addNewItem(1, itemDto);
        } catch (BadRequestException exp) {
            assertEquals("Передаваемый itemDto не может быть null.", exp.getMessage());
        }

        try {
            item.setName(null);
        } catch (IllegalArgumentException exp) {
            assertEquals("name is marked non-null but is null", exp.getMessage());
        }

        try {
            item.setDescription(null);
        } catch (IllegalArgumentException exp) {
            assertEquals("description is marked non-null but is null", exp.getMessage());
        }

        try {
            item.setAvailable(null);
        } catch (IllegalArgumentException exp) {
            assertEquals("available is marked non-null but is null", exp.getMessage());
        }
    }

    @Test
    void updateItem() {
        ItemDto itemDto = new ItemDto("Test", "TestDescription", true, null);
        itemController.addNewItem(1, itemDto);

        itemDto = new ItemDto("Test2", "TestDescription2", false, null);
        Item item = itemController.changeItem(1, itemDto, 1);

        assertEquals(1, item.getId());
        assertEquals(1, item.getOwnerId());
        assertEquals("Test2", item.getName());
        assertEquals("TestDescription2", item.getDescription());
        assertFalse(item.getAvailable());

        try {
            itemController.changeItem(1, itemDto, 2);
        } catch (NotFoundException exp) {
            assertEquals("Предмет с id равным 2 не найден.", exp.getMessage());
        }

        userController.addNewUser(new UserDto("TestUser2", "user@user.com"));

        try {
            itemController.changeItem(2, itemDto, 1);
        } catch (ForbiddenException exp) {
            assertEquals("Изменение полей предмета доступно только опубликовавшему его пользователю.",
                    exp.getMessage());
        }
    }

    @Test
    void getItem() {
        ItemDto itemDto = new ItemDto("Test", "TestDescription", true, null);
        itemController.addNewItem(1, itemDto);
        Item item = itemController.getItem(1);

        assertEquals(1, item.getId());
        assertEquals(1, item.getOwnerId());
        assertEquals("Test", item.getName());
        assertEquals("TestDescription", item.getDescription());
        assertTrue(item.getAvailable());

        try {
            itemController.getItem(2);
        } catch (NotFoundException exp) {
            assertEquals("Предмет с id равным 2 не найден.", exp.getMessage());
        }
    }

    @Test
    void getAllUsersItems() {
        ItemDto itemDto = new ItemDto("Test", "TestDescription", true, null);
        itemController.addNewItem(1, itemDto);
        itemDto = new ItemDto("Test2", "TestDescription2", false, null);
        itemController.addNewItem(1, itemDto);
        List<Item> items = itemController.getAllItemsOfUser(1);

        assertFalse(items.isEmpty());
        assertEquals(2, items.size());
        assertEquals("Test", items.get(0).getName());
        assertFalse(items.get(1).getAvailable());

        userController.addNewUser(new UserDto("TestUser2", "user@user.com"));

        items = itemController.getAllItemsOfUser(2);

        assertTrue(items.isEmpty());

        try {
            itemController.getAllItemsOfUser(3);
        } catch (NotFoundException exp) {
            assertEquals("Пользователь с id равным 3 не найден.", exp.getMessage());
        }
    }

    @Test
    void getAllItemsByTextRequest() {
        ;
        itemController.addNewItem(1, new ItemDto("Screwdriver", "Simple screwdriver",
                true, null));
        itemController.addNewItem(1, new ItemDto("Screwdriver", "Sonic screwdriver",
                true, null));
        itemController.addNewItem(1, new ItemDto("PoliceBox", "Blue PoliceBox",
                true, null));
        List<Item> items = itemController.getAllByTextRequest("DrIvEr");

        assertFalse(items.isEmpty());
        assertEquals(2, items.size());

        items = itemController.getAllByTextRequest("blue");

        assertEquals(1, items.size());
        assertEquals("PoliceBox", items.get(0).getName());

        itemController.changeItem(1, new ItemDto(null, null, false, null), 1);
        items = itemController.getAllByTextRequest("DrIvEr");

        assertEquals(1, items.size());
        assertEquals("Sonic screwdriver", items.get(0).getDescription());

        items = itemController.getAllByTextRequest(" ");

        assertTrue(items.isEmpty());
    } */

    @Test
    void contextLoads() {
    }

}
