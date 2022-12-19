package ru.yandex.practicum.filmorate.storage.genres;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

@Component
public class GenresDBStorage implements GenresStorage {

    JdbcTemplate jdbcTemplate;

    public GenresDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private int currentId(){
        int count = 0;
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT max(genre_id) as m FROM genres");
        if(mpaRows.next()){
            count = mpaRows.getInt("m");
        }
        return count;
    }

    @Override
    public List<Genre<Integer, String>> getGenres() {
        List<Genre<Integer, String>> getGenresList = new ArrayList<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM GENRES");
        while (genreRows.next()) {
            String name = genreRows.getString("genre");
            int id = genreRows.getInt("genre_id");
            Genre<Integer, String> genre = new Genre<>(id, name);
            getGenresList.add(genre);
        }
        return getGenresList;
    }

    @Override
    public Genre<Integer, String> getGenreById(Integer id) {
        if (id<=currentId()) {
            SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genres WHERE genre_id = ?", id);
            if (genreRows.next()) {
                String name = genreRows.getString("genre");
                return new Genre<>(id, name);
            } else {
                return null;
            }
        } else {
            throw new NotFoundException("Нет такого id");
        }
    }
}
