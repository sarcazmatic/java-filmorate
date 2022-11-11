package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private static UserController userController;
    private static User user;


    @BeforeAll
    static void prepare() {
        userController = new UserController();
        user = User.builder()
                .login("login")
                .birthday(LocalDate.of(1995, 12, 28))
                .email("what@what.ru")
                .name("Valid Vlad")
                .id(44)
                .build();
    }

    @Test
    void usersGet() {
        userController.getUsers();
        assertNotNull(userController.getUsers());
    }

    @Test
    void postUserEmptyName() {
        User userEmptyName = user.toBuilder().name(" ").build();
        userController.postUsers(userEmptyName);
        assertTrue(userController.getUsers().contains(userEmptyName));
    }

    @Test
    void postUser() {
        userController.postUsers(user);
        assertTrue(userController.getUsers().contains(user));
    }

    @Test
    void postUserEmptyLogin() {
        User userEmptyLogin = user.toBuilder().login(" ").build();
        final ValidationException e = assertThrows(ValidationException.class, () -> userController.postUsers(userEmptyLogin));
    }

    @Test
    void postUserLoginWithSpaces() {
        User userLoginWithSpaces = user.toBuilder().login("qq qq").build();
        final ValidationException e = assertThrows(ValidationException.class, () -> userController.postUsers(userLoginWithSpaces));
    }

    @Test
    void postUserEmailWithoutAtSymbol() {
        User userInvalidEmail = user.toBuilder().email("waah").build();
        final ValidationException e = assertThrows(ValidationException.class, () -> userController.postUsers(userInvalidEmail));
    }

    @Test
    void postUserFromFuture() {
        User userFromFuture = user.toBuilder().birthday(LocalDate.now().plusDays(1)).build();
        final ValidationException e = assertThrows(ValidationException.class, () -> userController.postUsers(userFromFuture));
    }

    @Test
    void putUserEmptyName() {
        User userPutEmptyName = user.toBuilder().name(" ").build();
        userPutEmptyName.setId(user.getId());
        userController.putUsers(userPutEmptyName);
        assertTrue(userController.getUsers().contains(userPutEmptyName));
        assertFalse(userController.getUsers().contains(user));
    }

    @Test
    void putUser() {
        User userPut = user.toBuilder().name("Put Name").build();
        for (User u : userController.getUsers()) {
            if (u.getId() != 0) {
                userPut.setId(u.getId());
                userController.putUsers(userPut);
                assertTrue(userController.getUsers().contains(userPut));
                assertFalse(userController.getUsers().contains(user));
            }
        }
    }

    @Test
    void putUserEmptyLogin() {
        User userEmptyLogin = user.toBuilder().login(" ").build();
        final ValidationException e = assertThrows(ValidationException.class, () -> userController.putUsers(userEmptyLogin));
    }

    @Test
    void putUserLoginWithSpaces() {
        User userLoginWithSpaces = user.toBuilder().login("qq qq").build();
        final ValidationException e = assertThrows(ValidationException.class, () -> userController.putUsers(userLoginWithSpaces));
    }

    @Test
    void putUserEmailWithoutAtSymbol() {
        User userInvalidEmail = user.toBuilder().email("waah").build();
        final ValidationException e = assertThrows(ValidationException.class, () -> userController.putUsers(userInvalidEmail));
    }

    @Test
    void putUserFromFuture() {
        User userFromFuture = user.toBuilder().birthday(LocalDate.now().plusDays(1)).build();
        final ValidationException e = assertThrows(ValidationException.class, () -> userController.putUsers(userFromFuture));
    }
}