package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private static Map<Integer, User> users = new HashMap<>();
    private static int id = 1;

    @GetMapping
    public static List<User> getUsers() {
        return List.copyOf(users.values());
    }

    @PostMapping
    public static User postUsers(@RequestBody User user) {
        userValidate(user);
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: " + user);
        return users.get(user.getId());
    }

    @PutMapping
    public User putUsers(@RequestBody User user) {
        userValidate(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Обновлен пользователь: " + user);
            return users.get(user.getId());
        } else {
            throw new ValidationException("Юзера с таким ID не существует");
        }
    }

    private static void userValidate(User user) {
        if (user.getName() == null || StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
            log.debug("Вот тут ловим пустое имя и меняем на " + user.getName());
        }
        if (StringUtils.isBlank(user.getEmail())) {
            log.error("Ошибка валидации пользователя: e-mail пуст/из пробелов");
            throw new ValidationException("Пользователь не соответствует критериям: e-mail пуст или состоит из пробелов");
        } else if (!StringUtils.contains(user.getEmail(), '@')) {
            log.error("Ошибка валидации пользователя: в e-mail отсутствует @");
            throw new ValidationException("Пользователь не соответствует критериям: e-mail не содержит @");
        } else if (StringUtils.isBlank(user.getLogin())) {
            log.error("Ошибка валидации пользователя: логин пуст");
            throw new ValidationException("Пользователь не соответствует критериям: логин пуст");
        } else if (StringUtils.contains(user.getLogin(), ' ')) {
            log.error("Ошибка валидации пользователя: логин содержит пробелы");
            throw new ValidationException("Пользователь не соответствует критериям: пробелы в логине недопустимы");
        } else if (user.getBirthday().isAfter(LocalDate.now()) || user.getBirthday() == null) {
            log.error("Ошибка валидации пользователя: дата рождения не наступила");
            throw new ValidationException("Пользователь не соответствует критериям: нельзя родиться в будущем");
        }
    }

}
