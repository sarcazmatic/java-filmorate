package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.URLParametersException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public Map<Integer, User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    public User postUsers(User user) {
        return userStorage.postUsers(user);
    }

    public User putUsers(User user) {
        return userStorage.putUsers(user);
    }

    public void deleteUsers(User user) {
        userStorage.deleteUsers(user);
    }

    public void addFriends(Integer id, Integer friendId) {
        if (!id.equals(friendId)) {
            if (userStorage.getUsers().containsKey(id) && userStorage.getUsers().containsKey(friendId)) {
                userStorage.putFriend(id, friendId);
            } else {
                throw new NotFoundException("Объект не найден");
            }
        } else {
            throw new URLParametersException("Переданы одинаковые id");
        }
    }

    public void removeFriends(Integer id, Integer friendId) {
        if (!id.equals(friendId)) {
            userStorage.removeFriends(id, friendId);
        } else {
            throw new URLParametersException("Переданы одинаковые id");
        }
    }

    public List<User> getUserFriends(Integer id) {
        List<User> friends = new ArrayList<>();
        if (userStorage.getUserById(id).getFriendship() != null) {
            friends = userStorage.getUsersFriends(id);
        }
        return friends;
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        List<User> commonFriends = new ArrayList<>();
        List<User> iDfriends = new ArrayList<>(getUserFriends(id));
        List<User> otherIdFriends = new ArrayList<>(getUserFriends(otherId));
        if (getUserFriends(id) != null && getUserFriends(otherId) != null) {
            for (User u1 : iDfriends) {
                for (User u2 : otherIdFriends) {
                    if (u1.getId() == u2.getId()) {
                        commonFriends.add(u1);
                    }
                }
            }
        }
        return commonFriends;
    }

}
