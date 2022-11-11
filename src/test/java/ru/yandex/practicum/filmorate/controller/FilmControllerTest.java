package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private static FilmController filmController;
    private static Film film;


    @BeforeAll
    static void prepare() {
        filmController = new FilmController();
        film = Film.builder()
                .name("Тест")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .rate(4)
                .duration(100)
                .description("Тестовое описание")
                .id(404)
                .build();
    }

    @Test
    void filmsGet() {
        filmController.getFilms();
        assertNotNull(filmController.getFilms());
    }

    @Test
    void postFilmNegativeDuration() {
        Film filmNegativeDuration = film.toBuilder().duration(-2).build();
        final ValidationException e = assertThrows(ValidationException.class, () -> filmController.postFilms(filmNegativeDuration));
    }

    @Test
    void postFilm() {
        filmController.postFilms(film);
        assertTrue(filmController.getFilms().contains(film));
    }

    @Test
    void postFilmZeroDuration() {
        Film filmZeroDuration = film.toBuilder().name(" ").build();
        final ValidationException e = assertThrows(ValidationException.class, () -> filmController.postFilms(filmZeroDuration));
    }

    @Test
    void postFilmNoName() {
        Film filmNoName = film.toBuilder().name(" ").build();
        final ValidationException e = assertThrows(ValidationException.class, () -> filmController.postFilms(filmNoName));
    }

    @Test
    void postFilmNoDescription() {
        Film filmNoDescription = film.toBuilder().description(" ").build();
        final ValidationException e = assertThrows(ValidationException.class, () -> filmController.postFilms(filmNoDescription));
    }

    @Test
    void postFilmBeforeCutOff() {
        Film filmBeforeCutOf = film.toBuilder().releaseDate(LocalDate.of(1895, 12, 27)).build();
        final ValidationException e = assertThrows(ValidationException.class, () -> filmController.postFilms(filmBeforeCutOf));
    }

    @Test
    void putFilmNegativeDuration() {
        Film filmNegativeDuration = film.toBuilder().duration(-2).build();
        final ValidationException e = assertThrows(ValidationException.class, () -> filmController.putFilms(filmNegativeDuration));
    }

    @Test
    void postFilmDescriptionOver200() {
        Film filmNegativeDuration = film.toBuilder()
                .description("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901")
                .build();
        final ValidationException e = assertThrows(ValidationException.class, () -> filmController.postFilms(filmNegativeDuration));
    }

    @Test
    void putFilm() {
        Film filmPut = film.toBuilder().name("new name upd put").build();
        for (Film f : filmController.getFilms()) {
         if (f.getId() != 0) {
             filmPut.setId(f.getId());
             filmController.putFilms(filmPut);
             assertTrue(filmController.getFilms().contains(filmPut));
             assertFalse(filmController.getFilms().contains(film));
         }
         return;
        }
    }

    @Test
    void putFilmZeroDuration() {
        Film filmZeroDuration = film.toBuilder().name(" ").build();
        final ValidationException e = assertThrows(ValidationException.class, () -> filmController.putFilms(filmZeroDuration));
    }

    @Test
    void putFilmNoName() {
        Film filmNoName = film.toBuilder().name(" ").build();
        final ValidationException e = assertThrows(ValidationException.class, () -> filmController.putFilms(filmNoName));
    }

    @Test
    void putFilmNoDescription() {
        Film filmNoDescription = film.toBuilder().description(" ").build();
        final ValidationException e = assertThrows(ValidationException.class, () -> filmController.putFilms(filmNoDescription));
    }

    @Test
    void putFilmBeforeCutOff() {
        Film filmBeforeCutOf = film.toBuilder().releaseDate(LocalDate.of(1895, 12, 27)).build();
        final ValidationException e = assertThrows(ValidationException.class, () -> filmController.putFilms(filmBeforeCutOf));
    }

    @Test
    void putFilmDescriptionOver200() {
        Film filmNegativeDuration = film.toBuilder()
                .description("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901")
                .build();
        final ValidationException e = assertThrows(ValidationException.class, () -> filmController.putFilms(filmNegativeDuration));
    }

}
