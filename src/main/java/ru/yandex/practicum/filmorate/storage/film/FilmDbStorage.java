package ru.yandex.practicum.filmorate.storage.film;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;


import java.time.LocalDate;
import java.util.*;

@Component
@Primary
public class FilmDbStorage implements FilmStorage {

    private static final LocalDate CUT_OFF_DATE = LocalDate.of(1895, 12, 28);
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    public int getCurrentId() {
        int currentId = 0;
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select MAX(film_id) as m from films;");
        if (filmRows.next()) {
            currentId = filmRows.getInt("m");
        }
        return currentId;
    }

    public int getCurrentFilmGenreId() {
        int currentId = 0;
        SqlRowSet flRows = jdbcTemplate.queryForRowSet("select MAX(film_genre_id) as m from film_genre;");
        if (flRows.next()) {
            currentId = flRows.getInt("m");
        }
        return currentId;
    }

    public int getCurrentFilmLikeId() {
        int currentId = 0;
        SqlRowSet flRows = jdbcTemplate.queryForRowSet("select MAX(film_like_id) as m from film_likes;");
        if (flRows.next()) {
            currentId = flRows.getInt("m");
        }
        return currentId;
    }

    @Override
    public Map<Integer, Film> getFilms() {
        Map<Integer, Film> filmMap = new HashMap<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films as f LEFT OUTER JOIN ratings as r ON f.rating_id = r.rating_id LEFT OUTER JOIN film_likes as fl ON f.film_id = fl.film_id GROUP BY f.film_id, fl.film_like_id;");
        while (filmRows.next()) {
            int id = filmRows.getInt("film_id");
            Film film = createFilmFromSQL(filmRows);
            log.info("Найден фильм: {} {}", id, film.getName());
            filmMap.put(id, film);
        }
        return filmMap;
    }

    public Film createFilmFromSQL(SqlRowSet filmRows) {
        Film film = new Film(
                filmRows.getInt("film_id"),
                filmRows.getString("title"),
                filmRows.getString("description"),
                filmRows.getDate("release_date").toLocalDate(),
                filmRows.getInt("duration"),
                0,
                new ArrayList<User>(),
                new Mpa("name", filmRows.getInt("rating_id")),
                new ArrayList<>());
        film.setRate(findFilmsRate(film.getId()));
        film.setGenres(findFilmsGenres(film.getId()));
        film.setMpa(findFilmsMpa(film.getId()));
        film.setId(filmRows.getInt("film_id"));
        film.setLikes(findFilmsLikes(film));
        return film;
    }

    public List<Genre<Integer, String>> findFilmsGenres(Integer id) {
        List<Genre<Integer, String>> genres = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * FROM FILM_GENRE as fg LEFT OUTER JOIN GENRES as g ON fg.genre_id = g.genre_id WHERE fg.film_id = ?", id);
        while (filmRows.next()) {
            Genre genreCheck = new Genre<>(filmRows.getInt("genre_id"), filmRows.getString("genre"));
            if (!genres.contains(genreCheck)){
                genres.add(genreCheck);
            }
        }
        System.out.println(genres);
        return genres;
    }

    public int findFilmsRate(Integer id) {
        int rate = 0;
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * FROM FILMS as f WHERE film_id = ?", id);
        if (filmRows.next()) {
            rate = filmRows.getInt("rate");
        }
        return rate;
    }

    public Mpa<String, Integer> findFilmsMpa(Integer id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * FROM FILMS as F LEFT OUTER JOIN RATINGS as R on f.rating_id=r.rating_id WHERE f.film_id = ?", id);
        if (filmRows.next()) {
            return new Mpa(filmRows.getString("rating"), filmRows.getInt("rating_id"));
        } else {
            return null;
        }
    }

    public List<User> findFilmsLikes(Film film) {
        List<User> likedFilm = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * FROM FILM_LIKES as fl WHERE fl.film_id = ?", film.getId());
        while (filmRows.next()) {
            likedFilm.add(userStorage.getUserById(filmRows.getInt("user_id")));
        }
        return likedFilm;
    }

    @Override
    public Film postFilms(Film film) {
        filmValidate(film);
        int rateTemp = film.getRate();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films as f LEFT JOIN ratings as r ON f.RATING_ID=r.RATING_ID WHERE f.title = ? AND f.release_date = ?", film.getName(), film.getReleaseDate());
        int currentId = (getCurrentId() + 1);
        if (!filmRows.next()) {
            jdbcTemplate.execute(
                    "MERGE INTO FILMS (film_id, title, description, release_date, duration, rate, rating_id)" +
                            "VALUES ('" + currentId + "', '" + film.getName() + "', '" + film.getDescription() + "', '" + film.getReleaseDate() + "', '" + film.getDuration() + "', '" + rateTemp + "', '" + film.getMpa().getId() + "');"
            );
        }
        try {
            List<Genre<Integer, String>> genresTemp = film.getGenres();
            for (Genre<Integer, String> g : genresTemp) {
                jdbcTemplate.execute(
                        "MERGE INTO FILM_GENRE (film_genre_id, genre_id, film_id)" +
                                "VALUES ('" + (getCurrentFilmGenreId() + 1) + "', '" + g.getId() + "', '" + currentId + "');");
            }
        } catch (RuntimeException e) {
        }
        return getFilmById(currentId);
    }

    @Override
    public Film putFilms(Film film) {
        filmValidate(film);
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films as f LEFT OUTER JOIN ratings as r ON f.rating_id = r.rating_id WHERE f.film_id = ?;", film.getId());
        if (filmRows.next()) {
            int ratingId = film.getMpa().getId();
            jdbcTemplate.execute("MERGE INTO FILMS (film_id, title, description, release_date, duration, rate, rating_id) " +
                    "VALUES ('" + film.getId() + "', '" + film.getName() + "', '" + film.getDescription() + "', '" + film.getReleaseDate() + "', '" + film.getDuration() + "', '" + film.getRate() + "', '" + ratingId + "');");
            try {
                List<Genre<Integer, String>> tempGenresUpd = film.getGenres();
                jdbcTemplate.execute("DELETE FROM FILM_GENRE WHERE film_id = '" + film.getId() + "';");
                for (Genre g : tempGenresUpd) {
                    jdbcTemplate.execute("MERGE INTO FILM_GENRE (film_genre_id, genre_id, film_id)" +
                            "VALUES ('" + (getCurrentFilmGenreId() + 1) + "', '" + g.getId() + "', '" + film.getId() + "');");
                }
            } catch (RuntimeException e) {
            }
            try {
                List<User> likesUpd = film.getLikes();
                jdbcTemplate.execute("DELETE FROM FILM_LIKES WHERE film_id = '" + film.getId() + "';");
                for (User u : likesUpd) {
                    jdbcTemplate.execute("MERGE INTO FILM_LIKES (film_like_id, film_id, user_id)" +
                            "VALUES ('" + (getCurrentFilmLikeId() + 1) + "', '" + film.getId() + "', '" + u.getId() + "');");
                }
            } catch (RuntimeException e) {
            }
            return getFilmById(film.getId());
        } else {
            log.info("Фильм с именем {} и датой выхода {} не найден.", film.getName(), film.getReleaseDate());
            throw new NotFoundException("Не нашли подходящего фильма");
        }
    }

    @Override
    public void deleteFilms(Film film) {
        String sourceFilmName = film.getName();
        LocalDate sourceFilmDate = film.getReleaseDate();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films as f LEFT OUTER JOIN ratings as r ON f.rating_id = r.rating_id WHERE f.title = ? GROUP BY f.film_id", film.getName());
        if (filmRows.next()) {
            Film dbFilm = createFilmFromSQL(filmRows);
            if (dbFilm.getName().equals(sourceFilmName) && dbFilm.getReleaseDate().equals(sourceFilmDate)) {
                jdbcTemplate.execute("DELETE FROM films WHERE TITLE = '" + film.getName() + "' AND RELEASE_DATE = '" + film.getReleaseDate() + "';");
            } else {
                throw new ValidationException("Фильмы не совпадают");
            }
        } else {
            throw new NotFoundException("Такого фильма в базе нет, удалять нечего");
        }
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

    @Override
    public Film getFilmById(Integer id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films as f WHERE f.film_id = ?;", id);
        if (filmRows.next()) {
            Film film = createFilmFromSQL(filmRows);
            film.setId(id);
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return film;
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            return null;
        }
    }

}
