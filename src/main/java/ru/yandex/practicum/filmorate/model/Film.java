package ru.yandex.practicum.filmorate.model;

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

    @EqualsAndHashCode.Exclude
    private int id;
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @NonNull
    private int duration;
    @EqualsAndHashCode.Exclude
    private int rate;
    @EqualsAndHashCode.Exclude
    private List<String> likes = new ArrayList<>();
    private Rating rating;
    private List<Genre> genre;

    public List<String> getLikes(){
        return likes;
    }

}
