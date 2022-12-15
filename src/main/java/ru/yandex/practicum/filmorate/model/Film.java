package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class Film {
    private int id;
    private @NonNull String name;
    private @NonNull String description;
    private @NonNull LocalDate releaseDate;
    private @NonNull int duration;
    private int rate;
    @JsonIgnore
    private List<User> likes;
    private Rating rating;
    private List<Genre> genre;

    public List<User> getLikes(){
        return likes;
    }

}
