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
    private final @NonNull String name;
    private final @NonNull String description;
    private final @NonNull LocalDate releaseDate;
    private final @NonNull int duration;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @Builder.Default
    private int rate = 0;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private List<User> likes = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private Mpa mpa = new Mpa("NULL", 0);
    @Builder.Default
    private List<Genre> genres = new ArrayList<>();
}
