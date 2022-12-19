package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.GenresService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")
public class GenresController {

    private final GenresService genresService;

    @GetMapping
    public List<Genre<Integer, String>> getGenres() {
        return List.copyOf(genresService.getGenres());
    }

    @GetMapping("/{id}")
    public Genre<Integer, String> getGenreById(@PathVariable int id) {
        return genresService.getGenreById(id);
    }


}
