package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Map<FriendshipStatus, List<User>> friendship;

    public void setFriendship(Map<FriendshipStatus, List<User>> newFriendship) {
        friendship = newFriendship;
    }

    public Map<FriendshipStatus, List<User>> getFriendship() {
        return friendship;
    }

}
