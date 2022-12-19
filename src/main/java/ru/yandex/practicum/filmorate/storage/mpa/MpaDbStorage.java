package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

@Component
public class MpaDbStorage implements MpaStorage {

    JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private int currentId(){
        int count = 0;
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT max(rating_id) as m FROM RATINGS");
        if(mpaRows.next()){
            count = mpaRows.getInt("m");
        }
        return count;
    }

    @Override
    public List<Mpa<String, Integer>> getMpa() {
        List<Mpa<String, Integer>> getMpaList = new ArrayList<>();
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM RATINGS");
        while (mpaRows.next()) {
            String name = mpaRows.getString("rating");
            int id = mpaRows.getInt("rating_id");
            Mpa<String, Integer> mpa = new Mpa<>(name, id);
            getMpaList.add(mpa);
        }
        return getMpaList;
    }

    public Mpa<String, Integer> getMpaById(Integer id) {
        if(id<=currentId()) {
            SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM RATINGS WHERE rating_id = ?", id);
            if (mpaRows.next()) {
                String name = mpaRows.getString("rating");
                return new Mpa<>(name, id);
            } else {
                return null;
            }
        } else {
            throw new NotFoundException("Нет такого id");
        }
    }
}
