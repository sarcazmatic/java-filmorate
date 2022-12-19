package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class Film {
    private int id;
    private final @NonNull String name;
    private final @NonNull String description;
    private final @NonNull LocalDate releaseDate;
    private final @NonNull int duration;
    @Builder.Default private int rate = 0;
//    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<User> likes;
    private Mpa<String, Integer> mpa;
    private List<Genre<Integer, String>> genres;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, int rate, List<User> likes, Mpa<String, Integer> mpa, List<Genre<Integer, String>> genres){
        this.id=id;
        this.name=name;
        this.description=description;
        this.releaseDate=releaseDate;
        this.duration=duration;
        this.rate=rate;
        this.likes=likes;
        this.mpa = mpa;
        this.genres=genres;
    }

    public void setGenres(List<Genre<Integer, String>> genres) {
        this.genres = genres;
    }

    public void setLikes (List<User> likes){
        this.likes = likes;
    }
}
