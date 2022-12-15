package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.URLParametersException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping({"/users"})
public class UserController {
    private final UserService userService;

    @GetMapping({"/{id}/friends"})
    public List<User> getUserFriends(@PathVariable Integer id) {
        return this.userService.getUserFriends(id);
    }

    @GetMapping({"/{id}/friends/common/{otherId}"})
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        if (!this.userService.getUsers().containsKey(id)) {
            throw new NotFoundException("Пользователь с  id " + id + " не найден");
        } else if (!this.userService.getUsers().containsKey(otherId)) {
            throw new NotFoundException("Пользователь с  id " + otherId + " не найден");
        } else if (id.equals(otherId)) {
            throw new URLParametersException("В параметрах переданы одинаковые id");
        } else {
            return this.userService.getCommonFriends(id, otherId);
        }
    }

    @PutMapping({"/{id}/friends/{friendId}"})
    public void addFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        if (id <= 0) {
            throw new NotFoundException("id не может быть меньше или равен нулю.");
        } else if (friendId <= 0) {
            throw new NotFoundException("id не может быть меньше или равен нулю.");
        } else {
            this.userService.addFriends(id, friendId);
        }
    }

    @DeleteMapping({"/{id}/friends/{friendId}"})
    public void removeFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        this.userService.removeFriends(id, friendId);
    }

    @GetMapping
    public List<User> getUsers() {
        return List.copyOf(this.userService.getUsers().values());
    }

    @GetMapping({"/{id}"})
    public User getUserById(@PathVariable Integer id) {
        if (!this.userService.getUsers().containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        } else {
            return this.userService.getUserById(id);
        }
    }

    @PostMapping
    public User postUsers(@RequestBody User user) {
        return this.userService.postUsers(user);
    }

    @DeleteMapping
    public void deleteUsers(@RequestBody User user) {
        this.userService.deleteUsers(user);
    }

    @PutMapping
    public User putUsers(@RequestBody User user) {
        return this.userService.putUsers(user);
    }
}
