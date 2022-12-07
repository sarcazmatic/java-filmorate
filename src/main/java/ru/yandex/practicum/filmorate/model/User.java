package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.*;


@Data
@Builder(toBuilder = true)
public class User {

    @EqualsAndHashCode.Exclude
    private int id;
    @Email
    @NonNull
    private String email;
    @NonNull
    private String login;
    private String name;
    @NonNull
    private LocalDate birthday;
    @EqualsAndHashCode.Exclude
    private Map<FriendshipStatus, List<Friend>> friendship;

    public void setFriendship(Map<FriendshipStatus, List<Friend>> newFriendship) {
        friendship = newFriendship;
    }

    public Map<FriendshipStatus, List<Friend>> getFriendship() {
        return friendship;
    }

}
