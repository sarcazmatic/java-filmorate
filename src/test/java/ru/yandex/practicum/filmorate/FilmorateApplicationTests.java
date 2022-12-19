package ru.yandex.practicum.filmorate;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    static LocalDate ld1;
    static User user1;
    static LocalDate ld2;
    static User user2;
    static LocalDate ld6;

    static Film film1;

    static Film film2;

    static Film film6;
    static User user6;

    static Mpa<String, Integer> mpa1;
    static Mpa<String, Integer> mpa2;

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @BeforeAll
    static void prepare(){
        mpa1 = new Mpa<>("G", 1);
        mpa2 = new Mpa<>("R", 5);
        ld1 = LocalDate.now().minusYears(20);
        user1 = new User(1, "vlad@yandex.ru", "validvlad", "valid vlad", ld1, null);
        ld2 = LocalDate.now().minusYears(35);
        user2 = new User(2, "lad@yandex.ru", "lidvlad", "lid vlad", ld2, null);
        ld6 = LocalDate.now().minusYears(25);
        user6 = new User(6, "azure@friend.ru", "azure", "", ld6, null);
        film1 = new Film(1, "Любовь и голуби", "Про любовь и голубей", ld1, 120, 3, null, mpa1, null);
        film2 = new Film(2, "Разлука и голуби", "Про разлуку и голубей", ld2, 110, 5, null, mpa2, null);
        film6 = new Film(6, "Возвращение голубей", "Голуби наносят ответный удар", ld6, 30, 1, null, mpa2, null);

    }

    @Test
    public void postUser() {
        userStorage.postUsers(user1);
        userStorage.postUsers(user2);
        userStorage.postUsers(user6);
        assertThat(userStorage.getUserById(1).equals(user1));
        assertThat(userStorage.getUserById(2).equals(user2));
    }

    @Test
    public void testFindUsers() {
        Map<Integer, User> users = userStorage.getUsers();
        assertThat(users.containsKey(1) && users.containsKey(2) && users.containsValue(user1) && users.containsValue(user2));
    }

    @Test
    public void testFindUserById() {
        assertThat(userStorage.getUserById(user2.getId())).hasFieldOrPropertyWithValue("id", user2.getId());
    }

    @Test
    public void putUsers() {
        User user3 = user1.toBuilder().name("хахаха а вот и не влад").email("vlad@vladmail.com").build();
        userStorage.putUsers(user3);
        assertThat(userStorage.getUserById(1).equals(user3));
    }

    @Test
    public void testDeleteUser() {
        userStorage.deleteUsers(user2);
        assertThat(!userStorage.getUsers().containsKey(2) && !userStorage.getUsers().containsValue(user2));
    }

    @Test
    public void testPostFilms() {
        filmStorage.postFilms(film1);
        filmStorage.postFilms(film2);
    }
    @Test
    public void testGetFilms() {
        assertThat(filmStorage.getFilms().values().contains(film1) && filmStorage.getFilms().values().contains(film2));
    }

    @Test
    public void putFilms() {
        Film filmUpd = film1.toBuilder().description("Очень любвиобильные голуби").build();
        filmStorage.putFilms(filmUpd);
        assertThat(filmStorage.getFilms().values().contains(filmUpd) && !filmStorage.getFilms().values().contains(film1));
    }

    @Test
    public void getFilmById() {
        assertThat(filmStorage.getFilmById(1).equals(film1) || filmStorage.getFilmById(1).equals(film6));
    }

}
