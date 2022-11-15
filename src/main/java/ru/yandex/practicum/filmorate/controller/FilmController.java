package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private static final LocalDate CUT_OFF_DATE = LocalDate.of(1895, 12, 28);
    private static Map<Integer, Film> films = new HashMap<>();
    private static int id = 1;

    @GetMapping
    public static List<Film> getFilms() {
        return List.copyOf(films.values());
    }

    @ResponseBody
    @PostMapping
    public static Film postFilms(@RequestBody Film film) {
        filmValidate(film);
        film.setId(id++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: " + film);
        return films.get(film.getId());
    }

    @PutMapping
    public Film putFilms(@RequestBody Film film) {
        filmValidate(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Обновлен пользователь: " + film);
            return films.get(film.getId());
        } else {
            throw new ValidationException("Фильма с таким ID не существует");
        }
    }

    private static void filmValidate(Film film) {
        if (StringUtils.isBlank(film.getName())) {
            log.error("Ошибка валидации фильма: название фильма");
            throw new ValidationException("Ошибка валидации фильма: названия фильма пустое или состоит из пробелов.");
        } else if (StringUtils.isBlank(film.getDescription())) {
            log.error("Ошибка валидации фильма: описание фильма");
            throw new ValidationException("Ошибка валидации фильма: описание фильма пустое или состоит из пробелов.");
        } else if (StringUtils.length(film.getDescription()) > 200) {
            log.error("Ошибка валидации фильма: длина описания > 200");
            throw new ValidationException("Ошибка валидации фильма: описание фильма превышает 200 символов");
        } else if (film.getReleaseDate().isBefore(CUT_OFF_DATE) || film.getReleaseDate() == null) {
            log.error("Ошибка валидации фильма: неверная дата издания");
            throw new ValidationException("Ошибка валидации фильма: фильм создан до зарождения кино");
        } else if (film.getDuration() <= 0) {
            log.error("Ошибка валидации фильма: продолжительность фильма <= 0 секунд");
            throw new ValidationException("Ошибка валидации фильма: продолжительность фильма <= 0 секунд");
        }
    }

}
