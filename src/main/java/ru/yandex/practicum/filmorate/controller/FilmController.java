package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        return List.copyOf(filmService.getFilms().values());
    }

    @PostMapping
    public Film postFilms(@RequestBody Film film) {
        return filmService.postFilms(film);
    }

    @PutMapping
    public Film putFilms(@RequestBody Film film) {
        return filmService.putFilms(film);
    }

    @DeleteMapping
    public void deleteFilms(@RequestBody Film film) {
        filmService.deleteFilms(film);
    }


    @PutMapping("/{id}/like/{userId}")
    public List<User> likeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.likeFilm(id, userId);
    }


    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularMovies(@RequestParam(required = false, defaultValue = "10") String count) {
        return filmService.getPopularMovies(Integer.parseInt(count));
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        if (!filmService.getFilms().containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        } else {
            return filmService.getFilmById(id);
        }
    }

}
