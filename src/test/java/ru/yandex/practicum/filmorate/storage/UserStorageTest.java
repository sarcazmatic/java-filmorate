package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserStorageTest {

    private static UserStorage userStorage;
    private static User user;


    @BeforeAll
    static void prepare() {
        userStorage = new InMemoryUserStorage();
        user = User.builder()
                .login("login")
                .birthday(LocalDate.of(1995, 12, 28))
                .email("what@what.ru")
                .name("Valid Vlad")
                .id(44)
                .friendship(null)
                .build();
    }

    @Test
    void usersGet() {
        userStorage.getUsers();
        assertNotNull(userStorage.getUsers());
    }

    @Test
    void postUserEmptyName() {
        User userEmptyName = user.toBuilder().name(" ").build();
        userStorage.postUsers(userEmptyName);
        assertTrue(userStorage.getUsers().containsValue(userEmptyName));
    }

    @Test
    void postUser() {
        userStorage.postUsers(user);
        assertTrue(userStorage.getUsers().containsValue(user));
    }

    @Test
    void postUserEmptyLogin() {
        User userEmptyLogin = user.toBuilder().login(" ").build();
        final ValidationException e = assertThrows(ValidationException.class, () -> userStorage.postUsers(userEmptyLogin));
        assertEquals(e.getMessage(), "Пользователь не соответствует критериям: логин пуст");
    }

    @Test
    void postUserLoginWithSpaces() {
        User userLoginWithSpaces = user.toBuilder().login("qq qq").build();
        final ValidationException e = assertThrows(ValidationException.class, () -> userStorage.postUsers(userLoginWithSpaces));
        assertEquals(e.getMessage(), "Пользователь не соответствует критериям: пробелы в логине недопустимы");
    }

    @Test
    void postUserEmailWithoutAtSymbol() {
        User userInvalidEmail = user.toBuilder().email("waah").build();
        final ValidationException e = assertThrows(ValidationException.class, () -> userStorage.postUsers(userInvalidEmail));
        assertEquals(e.getMessage(), "Пользователь не соответствует критериям: e-mail не содержит @");
    }

    @Test
    void postUserFromFuture() {
        User userFromFuture = user.toBuilder().birthday(LocalDate.now().plusDays(1)).build();
        final ValidationException e = assertThrows(ValidationException.class, () -> userStorage.postUsers(userFromFuture));
        assertEquals(e.getMessage(), "Пользователь не соответствует критериям: нельзя родиться в будущем");
    }

    @Test
    void putUserEmptyName() {
        User userPutEmptyName = user.toBuilder().name(" ").build();
        userPutEmptyName.setId(user.getId());
        userStorage.putUsers(userPutEmptyName);
        assertTrue(userStorage.getUsers().containsValue(userPutEmptyName));
        assertFalse(userStorage.getUsers().containsValue(user));
    }

    @Test
    void putUser() {
        User userPut = user.toBuilder().name("Put Name").build();
        for (User u : userStorage.getUsers().values()) {
            if (u.getId() != 0) {
                userPut.setId(u.getId());
                userStorage.putUsers(userPut);
                assertTrue(userStorage.getUsers().containsValue(userPut));
                assertFalse(userStorage.getUsers().containsValue(user));
            }
        }
    }

    @Test
    void putUserEmptyLogin() {
        User userEmptyLogin = user.toBuilder().login(" ").build();
        final ValidationException e = assertThrows(ValidationException.class, () -> userStorage.putUsers(userEmptyLogin));
        assertEquals(e.getMessage(), "Пользователь не соответствует критериям: логин пуст");
    }

    @Test
    void putUserLoginWithSpaces() {
        User userLoginWithSpaces = user.toBuilder().login("qq qq").build();
        final ValidationException e = assertThrows(ValidationException.class, () -> userStorage.putUsers(userLoginWithSpaces));
        assertEquals(e.getMessage(), "Пользователь не соответствует критериям: пробелы в логине недопустимы");
    }

    @Test
    void putUserEmailWithoutAtSymbol() {
        User userInvalidEmail = user.toBuilder().email("waah").build();
        final ValidationException e = assertThrows(ValidationException.class, () -> userStorage.putUsers(userInvalidEmail));
        assertEquals(e.getMessage(), "Пользователь не соответствует критериям: e-mail не содержит @");
    }

    @Test
    void putUserFromFuture() {
        User userFromFuture = user.toBuilder().birthday(LocalDate.now().plusDays(1)).build();
        final ValidationException e = assertThrows(ValidationException.class, () -> userStorage.putUsers(userFromFuture));
        assertEquals(e.getMessage(), "Пользователь не соответствует критериям: нельзя родиться в будущем");
    }
}


