package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final String LIKE = "like";


    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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
        if (filmStorage.getFilms() == null) {
            popularFilms = new ArrayList<>();
            System.out.println(popularFilms + " Тест0");
        } else {
            popularFilms = new ArrayList<>(filmStorage.getFilms().values());
            System.out.println(popularFilms + " Тест1");
            popularFilms.sort(new PopularityComparator());
            System.out.println(popularFilms + " Тест1.5");
        }
        System.out.println(popularFilms + " Тест2");
        if (count > popularFilms.size()) {
            count = popularFilms.size();
        }
        System.out.println(count + " счет");
        List<Film> testingList = popularFilms.stream().limit(count).collect(Collectors.toList());
        System.out.println(testingList.size() + " размер");
        System.out.println(testingList + " список");
        return testingList;
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
        if (o2.getLikes().size() > o1.getLikes().size()) {
            return 1;
        } else if ((o2.getLikes().size() < o1.getLikes().size())) {
            return -1;
        } else {
            return 0;
        }
    }

}
