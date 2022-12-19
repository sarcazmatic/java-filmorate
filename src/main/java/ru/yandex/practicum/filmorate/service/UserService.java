package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
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

    /*private void createAndAddFriend(int idHost, int idFriend) {
        Map<FriendshipStatus, List<User>> newFriendship = new EnumMap<>(FriendshipStatus.class);
        List<User> idFriendsListPen = new ArrayList<>();
        List<User> idFriendsListAcc = new ArrayList<>();
        newFriendship.put(FriendshipStatus.PENDING, null);
        newFriendship.put(FriendshipStatus.ACCEPTED, null);
        if (userStorage.getUserById(idHost).getFriendship() != null) {
            if ((userStorage.getUserById(idHost).getFriendship().get(FriendshipStatus.PENDING)).contains(userStorage.getUserById(idFriend))) {
                throw new AlreadyAddedException("Друг уже дожидается апрува");
            }

            if ((userStorage.getUserById(idHost).getFriendship().get(FriendshipStatus.ACCEPTED)).contains(userStorage.getUserById(idFriend))) {
                throw new AlreadyAddedException("Друг уже дружит с вами");
            }

            idFriendsListPen = userStorage.getUserById(idHost).getFriendship().get(FriendshipStatus.PENDING);
            idFriendsListAcc = userStorage.getUserById(idHost).getFriendship().get(FriendshipStatus.ACCEPTED);
        }

        (idFriendsListPen).add(userStorage.getUserById(idFriend));
        newFriendship.put(FriendshipStatus.PENDING, idFriendsListPen);
        newFriendship.put(FriendshipStatus.ACCEPTED, idFriendsListAcc);
        userStorage.getUserById(idHost).setFriendship(newFriendship);
    }*/

    /* private void safelyRemoveFriends(Integer idHost, Integer idFriend) {
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
    }*/

}
