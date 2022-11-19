package ru.yandex.practicum.filmorate.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.format.DateTimeFormatters;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import javax.swing.text.DateFormatter;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        return List.copyOf(filmStorage.getFilms().values());
    }

    @PostMapping
    public Film postFilms(@RequestBody String jsonString) throws JsonProcessingException {
        Film film = createFilmOutOfBody(jsonString);
        return filmStorage.postFilms(film);
    }

    @PutMapping
    public Film putFilms(@RequestBody String jsonString) throws JsonProcessingException {
        Film film = createFilmOutOfBody(jsonString);
        return filmStorage.putFilms(film);
    }

    @DeleteMapping
    public void deleteFilms(@RequestBody String jsonString) throws JsonProcessingException {
        Film film = createFilmOutOfBody(jsonString);
        filmStorage.deleteFilms(film);
    }


    @PutMapping("/{id}/like/{userId}")
    public List<String> likeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
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
        if (!filmStorage.getFilms().containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        } else {
            return filmStorage.getFilmById(id);
        }
    }

    private Film createFilmOutOfBody(String jsonString) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        JsonNode jsonNode = om.readTree(jsonString);
        int rate = 0;
        int id = 0;
        Film film;
        try {
            rate = jsonNode.get("rate").asInt();
        } catch (RuntimeException e) {
            film = Film.builder()
                    .name(jsonNode.get("name").asText())
                    .description(jsonNode.get("description").asText())
                    .releaseDate(LocalDate.parse(jsonNode.get("releaseDate").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .duration(jsonNode.get("duration").asInt())
                    .rate(rate)
                    .build();
        }
        film = Film.builder()
                .name(jsonNode.get("name").asText())
                .description(jsonNode.get("description").asText())
                .releaseDate(LocalDate.parse(jsonNode.get("releaseDate").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .duration(jsonNode.get("duration").asInt())
                .rate(rate)
                .build();
        try {
            id = jsonNode.get("id").asInt();
        } catch (RuntimeException e) {
            return film.toBuilder()
                    .id(id)
                    .build();
        }
        return film.toBuilder()
                .id(id)
                .build();
    }
}
