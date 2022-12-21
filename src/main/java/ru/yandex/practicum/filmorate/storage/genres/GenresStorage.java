package ru.yandex.practicum.filmorate.storage.genres;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenresStorage {

    List<Genre> getGenres();

    Genre getGenreById(Integer id);

}
