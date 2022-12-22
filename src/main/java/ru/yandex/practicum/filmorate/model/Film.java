package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class Film {
    private int id;
    @NonNull
    private final String name;
    @NonNull
    private final String description;
    @NonNull
    private final LocalDate releaseDate;
    @NonNull
    private final int duration;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @Builder.Default
    private int rate = 0;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private List<User> likes = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Mpa mpa;
    @Builder.Default
    private List<Genre> genres = new ArrayList<>();
}
