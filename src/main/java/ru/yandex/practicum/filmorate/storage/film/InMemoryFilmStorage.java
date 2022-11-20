package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private static final LocalDate CUT_OFF_DATE = LocalDate.of(1895, 12, 28);
    private static final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Map<Integer, Film> getFilms() {
        return films;
    }

    @Override
    public Film postFilms(Film film) {
        film.setId(films.size() + 1);
        filmValidate(film);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: " + film);
        return films.get(film.getId());
    }

    @Override
    public Film putFilms(Film film) {
        filmValidate(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Обновлен пользователь: " + film);
            return films.get(film.getId());
        } else {
            throw new NotFoundException("Фильма с таким ID не существует");
        }
    }

    public void deleteFilms(@RequestBody Film film) {
        if (films.get(film.getId()).equals(film)) {
            films.remove(film.getId());
        } else {
            throw new NotFoundException("Нет такого фильма");
        }
    }

    @Override
    public Film getFilmById(Integer id) {
        return films.get(id);
    }

        @Override
    public void filmValidate(Film film) {
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
