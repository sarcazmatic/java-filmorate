package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genres.GenresStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenresService {

    private final GenresStorage genresStorage;

    public List<Genre> getGenres() {
        return List.copyOf(genresStorage.getGenres());
    }

    public Genre getGenreById(Integer id) {
        return genresStorage.getGenreById(id);
    }


}
