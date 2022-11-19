package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.yandex.practicum.filmorate.exception.ErrorHandler;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }


    @GetMapping("/{id}/friends")
    public List<Friend> getUserFriends(@PathVariable Integer id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<Friend> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("Пользователь с  id " + id + " не найден");
        } else if (!userStorage.getUsers().containsKey(otherId)) {
            throw new NotFoundException("Пользователь с  id " + otherId + " не найден");
        } else {
            return userService.getCommonFriends(id, otherId);
        }
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        if (id <= 0) {
            throw new NotFoundException("id не может быть меньше или равен нулю.");
        } if (friendId <= 0) {
            throw new NotFoundException("id не может быть меньше или равен нулю.");
        } else {
            userService.addFriends(id, friendId);
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.removeFriends(id, friendId);
    }

    @GetMapping
    public List<User> getUsers() {
        return List.copyOf(userStorage.getUsers().values());
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        } else {
            return userStorage.getUserById(id);
        }
    }

    @PostMapping
    public User postUsers(@RequestBody User user) {
        return userStorage.postUsers(user);
    }

    @DeleteMapping
    public void deleteUsers(@RequestBody User user) {
        userStorage.deleteUsers(user);
    }

    @PutMapping
    public User putUsers(@RequestBody User user) {
            return userStorage.putUsers(user);
    }

}
