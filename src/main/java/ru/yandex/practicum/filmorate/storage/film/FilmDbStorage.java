package ru.yandex.practicum.filmorate.storage.film;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;


import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private static final LocalDate CUT_OFF_DATE = LocalDate.of(1895, 12, 28);
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Film createFilmFromSQL(SqlRowSet filmRows) {
        Film film = new Film(
                filmRows.getInt("FILM_ID"),
                filmRows.getString("TITLE"),
                filmRows.getString("DESCRIPTION"),
                filmRows.getDate("RELEASE_DATE").toLocalDate(),
                filmRows.getInt("DURATION"),
                0,
                new ArrayList<>(),
                new Mpa("NAME", filmRows.getInt("RATING_ID")),
                new ArrayList<>()
        );
        int filmId = film.getId();
        film.setRate(findFilmsRate(filmId));
        film.setGenres(findFilmsGenres(filmId));
        film.setMpa(findFilmsMpa(filmId));
        film.setLikes(findFilmsLikes(filmId));
        return film;
    }

    @Override
    public Map<Integer, Film> getFilms() {
        Map<Integer, Film> filmMap = new HashMap<>();
        String sqlQuery = "SELECT * FROM FILMS;";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery);
        while (filmRows.next()) {
            Film film = createFilmFromSQL(filmRows);
            filmMap.put(film.getId(), film);
        }
        return filmMap;
    }

    @Override
    public Film getFilmById(Integer id) {
        String sqlQuery = "SELECT * FROM FILMS WHERE FILM_ID = ?;";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (filmRows.next()) {
            Film film = createFilmFromSQL(filmRows);
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return film;
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            throw new NotFoundException("Фильм с таким id не найден");
        }
    }

    public List<Genre> findFilmsGenres(Integer id) {
        List<Genre> genres = new ArrayList<>();
        String sqlQuery = "SELECT * FROM FILM_GENRE as fg LEFT OUTER JOIN GENRES as g ON fg.GENRE_ID = g.GENRE_ID " +
                "WHERE fg.FILM_ID = ?;";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        while (genreRows.next()) {
            Genre genreCheck = new Genre(
                    genreRows.getInt("GENRE_id"),
                    genreRows.getString("GENRE")
            );
            if (!genres.contains(genreCheck)) {
                genres.add(genreCheck);
            }
        }
        return genres;
    }

    public int findFilmsRate(Integer id) {
        int rate = 0;
        String sqlQuery = "SELECT * FROM FILMS WHERE FILM_ID = ?;";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (filmRows.next()) {
            rate = filmRows.getInt("RATE");
        }
        return rate;
    }

    public Mpa findFilmsMpa(Integer id) {
        String sqlQuery = "SELECT * FROM FILMS as f LEFT OUTER JOIN RATINGS as r ON f.RATING_ID=r.RATING_ID " +
                "WHERE f.FILM_ID = ?;";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (filmRows.next()) {
            return new Mpa(filmRows.getString("rating"), filmRows.getInt("rating_id"));
        } else {
            return null;
        }
    }

    public List<User> findFilmsLikes(Integer id) {
        String sqlQuery = "SELECT * FROM FILM_LIKES as fl LEFT JOIN USERS as u ON fl.USER_ID=u.USER_ID " +
                "WHERE fl.FILM_ID = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new User(
                        rs.getInt("USER_ID"),
                        rs.getString("EMAIL"),
                        rs.getString("LOGIN"),
                        rs.getString("NAME"),
                        rs.getDate("BIRTHDAY_DATE").toLocalDate(),
                        new ArrayList<>()),
                id);
    }

    @Override
    public Film postFilms(Film film) {
        filmValidate(film);
        String sqlQuery = "SELECT * FROM FILMS WHERE TITLE = ? AND RELEASE_DATE = ?;";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, film.getName(), film.getReleaseDate());
        if (!filmRows.next()) {
            String sqlQueryPost = "INSERT INTO FILMS (TITLE, DESCRIPTION, RELEASE_DATE, DURATION, RATE, RATING_ID) " +
                    "VALUES (?, ?, ? ,?, ? ,?);";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(sqlQueryPost, new String[]{"FILM_ID"});
                statement.setString(1, film.getName());
                statement.setString(2, film.getDescription());
                statement.setDate(3, Date.valueOf(film.getReleaseDate()));
                statement.setInt(4, film.getDuration());
                statement.setDouble(5, film.getRate());
                statement.setInt(6, film.getMpa().getId());
                return statement;
            }, keyHolder);
            film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
            Optional<List<Genre>> genresTemp = Optional.ofNullable(film.getGenres());
            if (genresTemp.isPresent()) {
                String sqlQueryFilmGenres = "INSERT INTO FILM_GENRE (GENRE_ID, FILM_ID)" +
                        "VALUES (?, ?);";
                for (Genre g : genresTemp.get()) {
                    jdbcTemplate.update(sqlQueryFilmGenres, g.getId(), film.getId());
                }
            }
        }
        return getFilmById(film.getId());
    }

    @Override
    public Film putFilms(Film film) {
        filmValidate(film);
        String sqlFilms = "SELECT * FROM FILMS as f LEFT OUTER JOIN RATINGS as r ON f.RATING_ID = r.RATING_ID " +
                "WHERE f.FILM_ID = ?;";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlFilms, film.getId());
        if (filmRows.next()) {
            String sqlPutFilms = "UPDATE FILMS SET TITLE = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, " +
                    "RATE = ?, RATING_ID = ? WHERE FILM_ID = ?;";
            jdbcTemplate.update(sqlPutFilms,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getRate(),
                    film.getMpa().getId(),
                    film.getId()
            );
            Optional<List<Genre>> tempGenresUpd = Optional.ofNullable(film.getGenres());
            if (tempGenresUpd.isPresent()) {
                String sqlGenresUDel = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
                jdbcTemplate.update(sqlGenresUDel, film.getId());
                String sqlGenresUpd = "INSERT INTO FILM_GENRE (GENRE_ID, FILM_ID) VALUES (?, ?);";
                for (Genre g : tempGenresUpd.get()) {
                    jdbcTemplate.update(sqlGenresUpd, g.getId(), film.getId());
                }
            }
            Optional<List<User>> likesUpd = Optional.ofNullable(film.getLikes());
            if (likesUpd.isPresent()) {
                String sqlLikesDel = "DELETE FROM FILM_LIKES WHERE FILM_ID = ?";
                jdbcTemplate.update(sqlLikesDel, film.getId());
                String sqlLikesUpd = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?);";
                for (User u : likesUpd.get()) {
                    jdbcTemplate.update(sqlLikesUpd, film.getId(), u.getId());
                }
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
        String sqlDeleteFilms = "SELECT * FROM FILMS as f LEFT OUTER JOIN RATINGS as r ON f.RATING.ID = r.RATING.ID " +
                "WHERE f.TITLE = ? GROUP BY f.FILM_ID;";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlDeleteFilms, film.getName());
        if (filmRows.next()) {
            Film dbFilm = createFilmFromSQL(filmRows);
            if (dbFilm.getName().equals(sourceFilmName) && dbFilm.getReleaseDate().equals(sourceFilmDate)) {
                String sqlRunDelete = "DELETE FROM FILMS WHERE TITLE = ? AND RELEASE_DATE = ?;";
                jdbcTemplate.update(sqlRunDelete, film.getName(), film.getReleaseDate());
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
        } else if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(CUT_OFF_DATE)) {
            log.error("Ошибка валидации фильма: неверная дата издания");
            throw new ValidationException("Ошибка валидации фильма: фильм создан до зарождения кино");
        } else if (film.getDuration() <= 0) {
            log.error("Ошибка валидации фильма: продолжительность фильма <= 0 секунд");
            throw new ValidationException("Ошибка валидации фильма: продолжительность фильма <= 0 секунд");
        }

    }

}
