package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Builder
@Data
@RequiredArgsConstructor
public class Mpa<String, Integer> {

    private String name;
    @Builder.Default private int id = 1;


    public Mpa(String name, int id){
        this.name = name;
        this.id = id;
    }

}
