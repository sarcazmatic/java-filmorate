package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final String LIKE = "like";

    public Map<Integer, Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film postFilms(Film film) {
        return filmStorage.postFilms(film);
    }

    public Film putFilms(Film film) {
        return filmStorage.putFilms(film);
    }

    public void deleteFilms(Film film) {
        filmStorage.deleteFilms(film);
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public List<String> likeFilm(Integer id, Integer userId) {
        List<String> likesList;
        if (filmStorage.getFilmById(id).getLikes() == null) {
            likesList = new ArrayList<>();
        } else {
            likesList = new ArrayList<>(filmStorage.getFilmById(id).getLikes());
        }
        likesList.add(userStorage.getUserById(userId).getEmail());
        Film film = filmStorage.getFilmById(id).toBuilder().likes(likesList).build();
        filmStorage.getFilms().replace(film.getId(), film);
        return filmStorage.getFilmById(id).getLikes();
    }

    public void deleteLike(Integer id, Integer userId) {
        if (userStorage.getUsers().containsKey(userId)) {
            filmStorage.getFilms().get(id).getLikes().remove(userStorage.getUsers().get(userId));
        } else {
            throw new NotFoundException("Нельзя удалить лайк от несуществующего пользователя!");
        }
    }

    public List<Film> getPopularMovies(Integer count) {
        List<Film> popularFilms;
        if (filmStorage.getFilms().isEmpty()) {
            popularFilms = new ArrayList<>();
        } else {
            popularFilms = new ArrayList<>(filmStorage.getFilms().values());
            popularFilms.sort(new PopularityComparator());
        }
        if (count > popularFilms.size()) {
            count = popularFilms.size();
        }
        return popularFilms.stream().limit(count).collect(Collectors.toList());
    }
}

class PopularityComparator implements Comparator<Film> {

    @Override
    public int compare(Film o1, Film o2) {
        if (o2.getLikes() == null && o1.getLikes() == null) {
            return 0;
        }
        if (o2.getLikes() == null) {
            return -1;
        }
        if (o1.getLikes() == null) {
            return -1;
        }
        return Integer.compare(o2.getLikes().size(), o1.getLikes().size());
    }

}
