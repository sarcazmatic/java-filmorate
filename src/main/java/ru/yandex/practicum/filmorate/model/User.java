package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.*;


@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class User {
    private int id;
    @Email
    @NonNull
    private String email;
    @NonNull
    private String login;
    private String name;
    @NonNull
    private LocalDate birthday;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private List<User> friendship = new ArrayList<>();

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
