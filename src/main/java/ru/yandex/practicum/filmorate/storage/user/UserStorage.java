package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {


    Map<Integer, User> getUsers();

    User getUserById(Integer id);

    User postUsers(User user);

    User putUsers(User user);

    void deleteUsers(User user);

    void userValidate(User user);

}
