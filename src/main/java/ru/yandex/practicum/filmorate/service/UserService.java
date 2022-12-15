package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.URLParametersException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
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

    public List<User> getUserFriends(Integer id) {
        List<User> friends = new ArrayList<>();
        if (userStorage.getUserById(id).getFriendship() != null) {
            if (userStorage.getUserById(id).getFriendship().containsKey(FriendshipStatus.PENDING)) {
                friends = userStorage.getUserById(id).getFriendship().get(FriendshipStatus.PENDING);
            }

            if (userStorage.getUserById(id).getFriendship().containsKey(FriendshipStatus.ACCEPTED)) {
                (friends).addAll(userStorage.getUserById(id).getFriendship().get(FriendshipStatus.ACCEPTED));
            }
        }

        return friends;
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        List<User> commonFriends = new ArrayList<>();
        if (getUserFriends(id) != null && getUserFriends(otherId) != null) {
            for (User u1 : getUserFriends(id)) {
                for (User u2 : getUserFriends(otherId)) {
                    if (u1.equals(u2)) {
                        commonFriends.add(u1);
                    }
                }
            }
        }

        return commonFriends;
    }

    private void createAndAddFriend(int idHost, int idFriend) {
        Map<FriendshipStatus, List<User>> newFriendship = new EnumMap<>(FriendshipStatus.class);
        List<User> idFriendsListPen = new ArrayList<>();
        List<User> idFriendsListAcc = new ArrayList<>();
        newFriendship.put(FriendshipStatus.PENDING, null);
        newFriendship.put(FriendshipStatus.ACCEPTED, null);
        if (userStorage.getUserById(idHost).getFriendship() != null) {
            if ((userStorage.getUserById(idHost).getFriendship().get(FriendshipStatus.PENDING)).contains(userStorage.getUsers().get(idFriend))) {
                throw new AlreadyAddedException("Друг уже дожидается апрува");
            }

            if ((userStorage.getUserById(idHost).getFriendship().get(FriendshipStatus.ACCEPTED)).contains(userStorage.getUsers().get(idFriend))) {
                throw new AlreadyAddedException("Друг уже дружит с вами");
            }

            idFriendsListPen = userStorage.getUserById(idHost).getFriendship().get(FriendshipStatus.PENDING);
            idFriendsListAcc = userStorage.getUserById(idHost).getFriendship().get(FriendshipStatus.ACCEPTED);
        }

        (idFriendsListPen).add(userStorage.getUsers().get(idFriend));
        newFriendship.put(FriendshipStatus.PENDING, idFriendsListPen);
        newFriendship.put(FriendshipStatus.ACCEPTED, idFriendsListAcc);
        userStorage.getUserById(idHost).setFriendship(newFriendship);
    }

    private void safelyRemoveFriends(Integer idHost, Integer idFriend) {
        if (userStorage.getUserById(idHost).getFriendship() != null) {
            Map<FriendshipStatus, List<User>> newFriendship = new EnumMap<>(FriendshipStatus.class);
            newFriendship.put(FriendshipStatus.PENDING, null);
            newFriendship.put(FriendshipStatus.ACCEPTED, null);
            List<User> idFriendsListPen = userStorage.getUserById(idHost).getFriendship().get(FriendshipStatus.PENDING);
            List<User> idFriendsListAcc = userStorage.getUserById(idHost).getFriendship().get(FriendshipStatus.ACCEPTED);
            if (idFriendsListPen.contains(userStorage.getUsers().get(idFriend))) {
                idFriendsListPen.remove(userStorage.getUsers().get(idFriend));
            } else {
                if (!idFriendsListAcc.contains(userStorage.getUsers().get(idFriend))) {
                    throw new NotFoundException("Друзья не найдены");
                }

                idFriendsListAcc.remove(userStorage.getUsers().get(idFriend));
            }

            newFriendship.put(FriendshipStatus.PENDING, idFriendsListPen);
            newFriendship.put(FriendshipStatus.ACCEPTED, idFriendsListAcc);
            (userStorage.getUsers().get(idHost)).setFriendship(newFriendship);
        } else {
            throw new NotFoundException("Список друзей и так пуст");
        }
    }

}
