package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserServiceTest {

    private static UserStorage userStorage;
    private static UserService userService;

    private static User user1;
    private static User user2;
    private static User user3;


    @BeforeAll
    static void prepare() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        user1 = User.builder()
                .login("login1")
                .birthday(LocalDate.of(1996, 12, 01))
                .email("one@one.ru")
                .name("One One")
                .id(1)
                .friendship(null)
                .build();
        user2 = User.builder()
                .login("login2")
                .birthday(LocalDate.of(1996, 12, 02))
                .email("two@two.ru")
                .name("Two Two")
                .id(2)
                .friendship(null)
                .build();
        user3 = User.builder()
                .login("login3")
                .birthday(LocalDate.of(1996, 12, 03))
                .email("three@three.ru")
                .name("Three Three")
                .id(3)
                .friendship(null)
                .build();
    }

    @Test
    void findCommonFriendsEmptyMapsBoth() {

        userStorage.postUsers(user1);
        userStorage.postUsers(user2);
        userStorage.postUsers(user3);
        System.out.println(userService.getUserFriends(user1.getId()));
        System.out.println(userService.getCommonFriends(user1.getId(), user2.getId()));


    }

}
