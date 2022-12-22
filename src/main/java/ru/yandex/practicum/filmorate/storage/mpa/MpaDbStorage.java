package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getMpa() {
        String sqlQuery = "SELECT * FROM RATINGS;";
        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> new Mpa(rs.getString("RATING"), rs.getInt("RATING_ID")));
    }

    @Override
    public Mpa getMpaById(Integer id) {
        String sqlQuery = "SELECT * FROM RATINGS WHERE rating_id = ?;";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (mpaRows.next()) {
            String name = mpaRows.getString("RATING");
            return new Mpa(name, id);
        } else {
            throw new NotFoundException("Нет такого id");
        }
    }
}
