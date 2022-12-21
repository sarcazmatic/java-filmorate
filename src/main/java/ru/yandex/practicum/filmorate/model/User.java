package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.*;


@Data
@Builder(toBuilder = true)
public class User {
    private int id;
    private @Email @NonNull String email;
    private @NonNull String login;
    private String name;
    private @NonNull LocalDate birthday;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default private List<User> friendship = new ArrayList<>();

    public User(int id, String email, String login, String name, LocalDate birthday, List<User> friendship){
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friendship = friendship;
    }

    public void setFriendship(List<User> newFriendship) {
        friendship = newFriendship;
    }

    public void addFriend(User user) {
        friendship.add(user);
    }

    public List<User> getFriendship() {
        return friendship;
    }

}
