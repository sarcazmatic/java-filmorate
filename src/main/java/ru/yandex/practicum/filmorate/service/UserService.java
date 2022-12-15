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
        return this.userStorage.getUsers();
    }

    public User getUserById(Integer id) {
        return this.userStorage.getUserById(id);
    }

    public User postUsers(User user) {
        return this.userStorage.postUsers(user);
    }

    public User putUsers(User user) {
        return this.userStorage.putUsers(user);
    }

    public void deleteUsers(User user) {
        this.userStorage.deleteUsers(user);
    }

    public void addFriends(Integer id, Integer friendId) {
        if (!id.equals(friendId)) {
            if (this.userStorage.getUsers().containsKey(id) && this.userStorage.getUsers().containsKey(friendId)) {
                this.createAndAddFriend(id, friendId);
                this.createAndAddFriend(friendId, id);
            } else {
                throw new NotFoundException("Объект не найден");
            }
        } else {
            throw new URLParametersException("Переданы одинаковые id");
        }
    }

    public void removeFriends(Integer id, Integer friendId) {
        if (!id.equals(friendId)) {
            this.safelyRemoveFriends(id, friendId);
            this.safelyRemoveFriends(friendId, id);
        } else {
            throw new URLParametersException("Переданы одинаковые id");
        }
    }

    public List<User> getUserFriends(Integer id) {
        List<User> friends = new ArrayList();
        if (this.userStorage.getUserById(id).getFriendship() != null) {
            if (this.userStorage.getUserById(id).getFriendship().containsKey(FriendshipStatus.PENDING)) {
                friends = (List)this.userStorage.getUserById(id).getFriendship().get(FriendshipStatus.PENDING);
            }

            if (this.userStorage.getUserById(id).getFriendship().containsKey(FriendshipStatus.ACCEPTED)) {
                ((List)friends).addAll((Collection)this.userStorage.getUserById(id).getFriendship().get(FriendshipStatus.ACCEPTED));
            }
        }

        return (List)friends;
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        List<User> commonFriends = new ArrayList();
        if (this.getUserFriends(id) != null && this.getUserFriends(otherId) != null) {
            Iterator var4 = this.getUserFriends(id).iterator();

            while(var4.hasNext()) {
                User u1 = (User)var4.next();
                Iterator var6 = this.getUserFriends(otherId).iterator();

                while(var6.hasNext()) {
                    User u2 = (User)var6.next();
                    if (u1.equals(u2)) {
                        commonFriends.add(u1);
                    }
                }
            }
        }

        return commonFriends;
    }

    private void createAndAddFriend(int idHost, int idFriend) {
        Map<FriendshipStatus, List<User>> newFriendship = new EnumMap(FriendshipStatus.class);
        List<User> idFriendsListPen = new ArrayList();
        List<User> idFriendsListAcc = new ArrayList();
        newFriendship.put(FriendshipStatus.PENDING, null);
        newFriendship.put(FriendshipStatus.ACCEPTED, null);
        if (this.userStorage.getUserById(idHost).getFriendship() != null) {
            if (((List)this.userStorage.getUserById(idHost).getFriendship().get(FriendshipStatus.PENDING)).contains(this.userStorage.getUsers().get(idFriend))) {
                throw new AlreadyAddedException("Друг уже дожидается апрува");
            }

            if (((List)this.userStorage.getUserById(idHost).getFriendship().get(FriendshipStatus.ACCEPTED)).contains(this.userStorage.getUsers().get(idFriend))) {
                throw new AlreadyAddedException("Друг уже дружит с вами");
            }

            idFriendsListPen = (List)this.userStorage.getUserById(idHost).getFriendship().get(FriendshipStatus.PENDING);
            idFriendsListAcc = (List)this.userStorage.getUserById(idHost).getFriendship().get(FriendshipStatus.ACCEPTED);
        }

        ((List)idFriendsListPen).add((User)this.userStorage.getUsers().get(idFriend));
        newFriendship.put(FriendshipStatus.PENDING, idFriendsListPen);
        newFriendship.put(FriendshipStatus.ACCEPTED, idFriendsListAcc);
        this.userStorage.getUserById(idHost).setFriendship(newFriendship);
    }

    private void safelyRemoveFriends(Integer idHost, Integer idFriend) {
        if (this.userStorage.getUserById(idHost).getFriendship() != null) {
            Map<FriendshipStatus, List<User>> newFriendship = new EnumMap(FriendshipStatus.class);
            newFriendship.put(FriendshipStatus.PENDING, null);
            newFriendship.put(FriendshipStatus.ACCEPTED, null);
            List<User> idFriendsListPen = (List)this.userStorage.getUserById(idHost).getFriendship().get(FriendshipStatus.PENDING);
            List<User> idFriendsListAcc = (List)this.userStorage.getUserById(idHost).getFriendship().get(FriendshipStatus.ACCEPTED);
            if (idFriendsListPen.contains(this.userStorage.getUsers().get(idFriend))) {
                idFriendsListPen.remove(this.userStorage.getUsers().get(idFriend));
            } else {
                if (!idFriendsListAcc.contains(this.userStorage.getUsers().get(idFriend))) {
                    throw new NotFoundException("Друзья не найдены");
                }

                idFriendsListAcc.remove(this.userStorage.getUsers().get(idFriend));
            }

            newFriendship.put(FriendshipStatus.PENDING, idFriendsListPen);
            newFriendship.put(FriendshipStatus.ACCEPTED, idFriendsListAcc);
            ((User)this.userStorage.getUsers().get(idHost)).setFriendship(newFriendship);
        } else {
            throw new NotFoundException("Список друзей и так пуст");
        }
    }

}
