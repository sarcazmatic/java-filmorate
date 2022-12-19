package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genres.GenresStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenresService {

    private final GenresStorage genresStorage;

    public List<Genre<Integer, String>> getGenres() {
        return List.copyOf(genresStorage.getGenres());
    }

    public Genre<Integer, String> getGenreById(@PathVariable Integer id) {
        return genresStorage.getGenreById(id);
    }


}
