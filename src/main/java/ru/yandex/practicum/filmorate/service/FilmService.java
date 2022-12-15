package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
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
        return this.filmStorage.getFilms();
    }

    public Film postFilms(Film film) {
        return this.filmStorage.postFilms(film);
    }

    public Film putFilms(Film film) {
        return this.filmStorage.putFilms(film);
    }

    public void deleteFilms(Film film) {
        this.filmStorage.deleteFilms(film);
    }

    public Film getFilmById(Integer id) {
        return this.filmStorage.getFilmById(id);
    }

    public List<User> likeFilm(Integer id, Integer userId) {
        ArrayList likesList;
        if (this.filmStorage.getFilmById(id).getLikes() == null) {
            likesList = new ArrayList();
        } else {
            likesList = new ArrayList(this.filmStorage.getFilmById(id).getLikes());
        }

        likesList.add(this.userStorage.getUserById(userId));
        Film film = this.filmStorage.getFilmById(id).toBuilder().likes(likesList).build();
        this.filmStorage.getFilms().replace(film.getId(), film);
        return this.filmStorage.getFilmById(id).getLikes();
    }

    public void deleteLike(Integer id, Integer userId) {
        if (this.userStorage.getUsers().containsKey(userId)) {
            ((Film)this.filmStorage.getFilms().get(id)).getLikes().remove(this.userStorage.getUsers().get(userId));
        } else {
            throw new NotFoundException("Нельзя удалить лайк от несуществующего пользователя!");
        }
    }

    public List<Film> getPopularMovies(Integer count) {
        ArrayList popularFilms;
        if (this.filmStorage.getFilms().isEmpty()) {
            popularFilms = new ArrayList();
        } else {
            popularFilms = new ArrayList(this.filmStorage.getFilms().values());
            popularFilms.sort(new PopularityComparator());
        }

        if (count > popularFilms.size()) {
            count = popularFilms.size();
        }

        return (List)popularFilms.stream().limit((long)count).collect(Collectors.toList());
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
