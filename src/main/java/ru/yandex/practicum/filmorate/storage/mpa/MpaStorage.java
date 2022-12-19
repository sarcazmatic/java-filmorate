package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {

    List<Mpa<String, Integer>> getMpa();

    Mpa<String, Integer> getMpaById(Integer id);

}
