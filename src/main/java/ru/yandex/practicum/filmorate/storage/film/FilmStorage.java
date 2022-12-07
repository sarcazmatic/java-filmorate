package ru.yandex.practicum.filmorate.storage.film;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {

    Map<Integer, Film> getFilms();

    Film postFilms(Film film);

    Film putFilms(Film film);

    void deleteFilms(Film film);

    void filmValidate(Film film);

    Film getFilmById(Integer id);

}
