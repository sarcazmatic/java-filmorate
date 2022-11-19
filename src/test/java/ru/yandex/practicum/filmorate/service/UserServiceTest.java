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
    private static User user1;
    private static User user2;
    private static User user3;


    @BeforeAll
    static void prepare() {
        userStorage = new InMemoryUserStorage();
        user1 = User.builder()
                .login("login1")
                .birthday(LocalDate.of(1996, 12, 01))
                .email("one@one.ru")
                .name("One One")
                .id(1)
                .friends(null)
                .build();
        user2 = User.builder()
                .login("login2")
                .birthday(LocalDate.of(1996, 12, 02))
                .email("two@two.ru")
                .name("Two Two")
                .id(2)
                .friends(null)
                .build();
        user3 = User.builder()
                .login("login3")
                .birthday(LocalDate.of(1996, 12, 03))
                .email("three@three.ru")
                .name("Three Three")
                .id(3)
                .friends(null)
                .build();
    }

    @Test
    public void addFriendsZeroFriends() {

        userStorage.postUsers(user1);
        userStorage.postUsers(user2);
        userStorage.postUsers(user3);


        Friend friend1 = Friend.builder()
                .birthday(userStorage.getUsers().get(1).getBirthday())
                .name(userStorage.getUsers().get(1).getName())
                .login(userStorage.getUsers().get(1).getLogin())
                .id(userStorage.getUsers().get(1).getId())
                .email(userStorage.getUsers().get(1).getEmail())
                .build();
        Friend friend2 = Friend.builder()
                .birthday(userStorage.getUsers().get(2).getBirthday())
                .name(userStorage.getUsers().get(2).getName())
                .login(userStorage.getUsers().get(2).getLogin())
                .id(userStorage.getUsers().get(2).getId())
                .email(userStorage.getUsers().get(2).getEmail())
                .build();
        Friend friend3 = Friend.builder()
                .birthday(userStorage.getUsers().get(3).getBirthday())
                .name(userStorage.getUsers().get(3).getName())
                .login(userStorage.getUsers().get(3).getLogin())
                .id(userStorage.getUsers().get(3).getId())
                .email(userStorage.getUsers().get(3).getEmail())
                .build();

        List<Friend> idFriendsList;
        if (userStorage.getUserById(1).getFriends() != null) {
            idFriendsList = new ArrayList<>(userStorage.getUserById(1).getFriends());
        } else {
            idFriendsList = new ArrayList<>();
        }
        idFriendsList.add(friend2);

        userStorage.getUsers().get(1).setFriends(idFriendsList);

        Assertions.assertEquals(userStorage.getUsers().get(1).getFriends(), List.of(friend2));


        List<Friend> idFriends2List = new ArrayList<>(userStorage.getUserById(1).getFriends());
        idFriends2List.add(friend3);


        userStorage.getUsers().get(1).setFriends(idFriends2List);

        Assertions.assertEquals(userStorage.getUsers().get(1).getFriends(), List.of(friend2, friend3));


    }

}
