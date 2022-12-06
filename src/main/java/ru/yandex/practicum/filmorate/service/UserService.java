package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.URLParametersException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

import static ru.yandex.practicum.filmorate.model.FriendshipStatus.ACCEPTED;
import static ru.yandex.practicum.filmorate.model.FriendshipStatus.PENDING;


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
        List<Friend> friends = new ArrayList<>();
        if (userStorage.getUserById(id).getFriendship() != null) {
            if (userStorage.getUserById(id).getFriendship().containsKey(PENDING)) {
                friends = userStorage.getUserById(id).getFriendship().get(PENDING);
            }
            if (userStorage.getUserById(id).getFriendship().containsKey(ACCEPTED)) {
                friends.addAll(userStorage.getUserById(id).getFriendship().get(ACCEPTED));
            }
        }
        return friends;
    }

    public List<Friend> getCommonFriends(Integer id, Integer otherId) {
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
    }

    private void createAndAddFriend(int idHost, int idFriend) {
        Friend friend = Friend.builder()
                .birthday(userStorage.getUsers().get(idFriend).getBirthday())
                .name(userStorage.getUsers().get(idFriend).getName())
                .login(userStorage.getUsers().get(idFriend).getLogin())
                .id(userStorage.getUsers().get(idFriend).getId())
                .email(userStorage.getUsers().get(idFriend).getEmail())
                .build();

        Map<FriendshipStatus, List<Friend>> newFriendship = new EnumMap<>(FriendshipStatus.class);
        List<Friend> idFriendsListPen = new ArrayList<>();
        List<Friend> idFriendsListAcc = new ArrayList<>();
        newFriendship.put(PENDING, null);
        newFriendship.put(ACCEPTED, null);

        if (userStorage.getUserById(idHost).getFriendship() != null) {
            if (userStorage.getUserById(idHost).getFriendship().get(PENDING).contains(friend)) {
                throw new AlreadyAddedException("Друг уже дожидается апрува");
            } else if (userStorage.getUserById(idHost).getFriendship().get(ACCEPTED).contains(friend)) {
                throw new AlreadyAddedException("Друг уже дружит с вами");
            }
            idFriendsListPen = userStorage.getUserById(idHost).getFriendship().get(PENDING);
            idFriendsListAcc = userStorage.getUserById(idHost).getFriendship().get(ACCEPTED);
        }
        idFriendsListPen.add(friend);
        newFriendship.put(PENDING, idFriendsListPen);
        newFriendship.put(ACCEPTED, idFriendsListAcc);
        userStorage.getUserById(idHost).setFriendship(newFriendship);
    }

    private void safelyRemoveFriends(Integer idHost, Integer idFriend) {

        if (userStorage.getUserById(idHost).getFriendship() != null) {
            Friend friend = Friend.builder()
                    .birthday(userStorage.getUsers().get(idFriend).getBirthday())
                    .name(userStorage.getUsers().get(idFriend).getName())
                    .login(userStorage.getUsers().get(idFriend).getLogin())
                    .id(userStorage.getUsers().get(idFriend).getId())
                    .email(userStorage.getUsers().get(idFriend).getEmail())
                    .build();


            Map<FriendshipStatus, List<Friend>> newFriendship = new EnumMap<>(FriendshipStatus.class);
            newFriendship.put(PENDING, null);
            newFriendship.put(ACCEPTED, null);
            List<Friend> idFriendsListPen = userStorage.getUserById(idHost).getFriendship().get(PENDING);
            List<Friend> idFriendsListAcc = userStorage.getUserById(idHost).getFriendship().get(ACCEPTED);
            if (idFriendsListPen.contains(friend)) {
                idFriendsListPen.remove(friend);
            } else if (idFriendsListAcc.contains(friend)) {
                idFriendsListAcc.remove(friend);
            } else {
                throw new NotFoundException("Друзья не найдены");
            }
            newFriendship.put(PENDING, idFriendsListPen);
            newFriendship.put(ACCEPTED, idFriendsListAcc);
            userStorage.getUsers().get(idHost).setFriendship(newFriendship);
        } else {
            throw new NotFoundException("Список друзей и так пуст");
        }

    }

}
