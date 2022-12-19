package ru.yandex.practicum.filmorate.storage.genres;

import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface GenresStorage {

    List<Genre<Integer, String>> getGenres();

    Genre<Integer, String> getGenreById(Integer id);

}
