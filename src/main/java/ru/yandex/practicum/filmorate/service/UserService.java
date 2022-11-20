package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.URLParametersException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
            if (userStorage.getUsers().containsKey(id)
                    && userStorage.getUsers().containsKey(friendId)) {

                createAndAddFriend(id, friendId);
                createAndAddFriend(friendId, id);

            } else {
                throw new NotFoundException("Объект не найден");
            }
        } else {
            throw new URLParametersException("Переданы одинаковые id");
        }
    }

    public void removeFriends(Integer id, Integer friendId) {
        if (!id.equals(friendId)) {
            safelyRemoveFriends(id, friendId);
            safelyRemoveFriends(friendId, id);
        } else {
            throw new URLParametersException("Переданы одинаковые id");
        }
    }

    public List<Friend> getUserFriends(Integer id) {
        return userStorage.getUserById(id).getFriends();
    }

    public List<Friend> getCommonFriends(Integer id, Integer otherId) {
        if (!id.equals(otherId)) {
            List<Friend> commonFriends = new ArrayList<>();
            if (getUserFriends(id) != null && getUserFriends(otherId) != null) {
                for (Friend u1 : getUserFriends(id)) {
                    for (Friend u2 : getUserFriends(otherId)) {
                        if (u1.equals(u2)) {
                            commonFriends.add(u1);
                        }
                    }
                }
            }
            return commonFriends;
        } else {
            throw new URLParametersException("Переданы одинаковые id");
        }
    }

    private void createAndAddFriend(int idHost, int idFriend) {
        Friend friend = Friend.builder()
                .birthday(userStorage.getUsers().get(idFriend).getBirthday())
                .name(userStorage.getUsers().get(idFriend).getName())
                .login(userStorage.getUsers().get(idFriend).getLogin())
                .id(userStorage.getUsers().get(idFriend).getId())
                .email(userStorage.getUsers().get(idFriend).getEmail())
                .build();


        List<Friend> idFriendsList;
        if (userStorage.getUserById(idHost).getFriends() != null) {
            idFriendsList = new ArrayList<>(userStorage.getUserById(idHost).getFriends());
            if (idFriendsList.contains(friend)) {
                idFriendsList.remove(friend);
            }
        } else {
            idFriendsList = new ArrayList<>();
        }
        idFriendsList.add(friend);

        userStorage.getUsers().get(idHost).setFriends(idFriendsList);
    }

    private void safelyRemoveFriends(Integer idHost, Integer idFriend) {
        if (userStorage.getUserById(idHost).getFriends() != null) {
            Friend friend = Friend.builder()
                    .birthday(userStorage.getUsers().get(idFriend).getBirthday())
                    .name(userStorage.getUsers().get(idFriend).getName())
                    .login(userStorage.getUsers().get(idFriend).getLogin())
                    .id(userStorage.getUsers().get(idFriend).getId())
                    .email(userStorage.getUsers().get(idFriend).getEmail())
                    .build();


            List<Friend> idFriendsList;
            idFriendsList = new ArrayList<>(userStorage.getUserById(idHost).getFriends());
            idFriendsList.remove(friend);

            userStorage.getUsers().get(idHost).setFriends(idFriendsList);
        } else {
            throw new URLParametersException("Переданы одинаковые id");
        }
    }

}
