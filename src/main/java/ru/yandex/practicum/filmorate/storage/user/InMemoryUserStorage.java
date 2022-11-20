package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private static final Map<Integer, User> users = new HashMap<>();

    @Override
    public Map<Integer, User> getUsers() {
        return users;
    }

    @Override
    public User getUserById(Integer id) {
        return users.get(id);
    }

    @Override
    public User postUsers(User user) {
        userValidate(user);
        user.setId(users.size() + 1);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: " + user);
        return users.get(user.getId());    }

    @Override
    public User putUsers(User user) {
        userValidate(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Обновлен пользователь: " + user);
            return users.get(user.getId());
        } else {
            throw new NotFoundException("Юзера с таким ID не существует");
        }
    }

    @Override
    public void deleteUsers(@RequestBody User user) {
        if (users.get(user.getId()).equals(user)) {
            users.remove(user.getId());
        } else {
            throw new NotFoundException("Нет такого пользователя");
        }
    }

    @Override
    public void userValidate(User user) {
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
