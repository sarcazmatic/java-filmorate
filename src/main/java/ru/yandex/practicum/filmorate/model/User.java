package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


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
    private List<Friend> friends = new ArrayList<>();

    public void setFriends(List<Friend> newFriends){
        friends = List.copyOf(newFriends);
    }

    public List<Friend> getFriends(){
        return friends;
    }

}
