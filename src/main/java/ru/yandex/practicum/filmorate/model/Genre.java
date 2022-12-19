package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Builder
@Data
@RequiredArgsConstructor
public class Genre<Integer, String> {

    private Integer id;
    private String name;

    public Genre(Integer id, String name){
        this.id = id;
        this.name=name;
    }

}
