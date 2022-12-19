package ru.yandex.practicum.filmorate.storage.user;

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
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
@Primary
public class UserDbStorage implements UserStorage {

    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getCurrentId() {
        int currentId = 0;
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select MAX(user_id) as c from users;");
        if (userRows.next()) {
            currentId = userRows.getInt("c");
        }
        return currentId;
    }

    public int getCurrentIdFriendships() {
        int currentId = 0;
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select MAX(friendship_id) as m from friendships;");
        if (userRows.next()) {
            currentId = userRows.getInt("m");
        }
        return currentId;
    }

    @Override
    public Map<Integer, User> getUsers() {
        Map<Integer, User> userMap = new HashMap<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users as u LEFT OUTER JOIN FRIENDSHIPS as f ON u.user_id = f.user_id");
        while (userRows.next()) {
            User user = createUserFromSQL(userRows);
            log.info("Найден пользователь: {} {}", user.getId(), user.getEmail());
            userMap.put(user.getId(), user);
        }
        return userMap;
    }


    @Override
    public User getUserById(Integer id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users as u WHERE u.user_id = ?", id);
        if (userRows.next()) {
            User user = createUserFromSQL(userRows);
            user.setFriendship(getUsersFriends(id));
            log.info("Найден пользователь: {} {}", user.getId(), user.getEmail());
            return user;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return null;
        }
    }

    @Override
    public List<User> getUsersFriends(Integer id) {
        List<User> userFriendships = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendships as f left join users as u on f.friend_id=u.user_id where f.user_id = ?", id);
        while (userRows.next()) {
            User user = createUserFromSQL(userRows);
            user.setId(userRows.getInt("friend_id"));
            userFriendships.add(user);
        }
        return userFriendships;
    }

    public User createUserFromSQL(SqlRowSet userRows) {
        return new User(
                userRows.getInt("user_id"),
                userRows.getString("email"),
                userRows.getString("login"),
                userRows.getString("name"),
                userRows.getDate("birthday_date").toLocalDate(),
                new ArrayList<>());
    }

    @Override
    public User postUsers(User user) {
        userValidate(user);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users as u LEFT OUTER JOIN FRIENDSHIPS as f ON u.user_id = f.user_id WHERE u.email = ?", user.getEmail());
        if (!userRows.next()) {
            int id = getCurrentId() + 1;
            String email = user.getEmail();
            String login = user.getLogin();
            String name = user.getName();
            LocalDate date = user.getBirthday();
            jdbcTemplate.execute(
                    "MERGE INTO USERS (USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY_DATE) " +
                            "VALUES ('" + id + "', '" + email + "', '" + login + "', '" + name + "', '" + date + "');"
            );
            return getUserById(id);
        } else {
            throw new NotFoundException("Такой пользователь уже имеется");
        }
    }

    @Override
    public User putUsers(User user) {
        userValidate(user);
        int id = user.getId();
        String email = user.getEmail();
        String login = user.getLogin();
        String name = user.getName();
        LocalDate date = user.getBirthday();

        try {
            User userDb = getUserById(id);
            if (user.getId() == userDb.getId()) {
                jdbcTemplate.execute(
                        "MERGE INTO USERS (USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY_DATE) " +
                                "VALUES ('" + id + "', '" + email + "', '" + login + "', '" + name + "', '" + date + "');"
                );
            } else {
                postUsers(user);
            }
        } catch (RuntimeException e) {
            throw new NotFoundException("Пользователь с таким e-mail и id не найден");
        }

        return getUserById(id);
    }

    @Override
    public void deleteUsers(User user) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", user.getId());
        if (userRows.next()) {
                jdbcTemplate.execute("DELETE FROM users WHERE user_id = '" + user.getId() + "';");
        } else {
            throw new NotFoundException("Пользователя с таким id нет, удалять нечего");
        }
    }

    @Override
    public void userValidate(User user) {
        if (user.getName() == null || StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
            log.debug("Вот тут ловим пустое имя и меняем на " + user.getName());
        }
        if (user.getId() <= 0) {
            user.setId(getCurrentId() + 1);
            log.debug("Идентификатор не может быть < или = 0");
        }
        if (StringUtils.isBlank(user.getEmail())) {
            log.error("Ошибка валидации пользователя: e-mail пуст/из пробелов");
            throw new ValidationException("Пользователь не соответствует критериям: e-mail пуст или состоит из пробелов");
        } else if (!StringUtils.contains(user.getEmail(), '@')) {
            log.error("Ошибка валидации пользователя: в e-mail отсутствует @");
            throw new ValidationException("Пользователь не соответствует критериям: e-mail не содержит @");
        } else if (StringUtils.isBlank(user.getLogin())) {
            log.error("Ошибка валидации пользователя: логин пуст");
            throw new ValidationException("Пользователь не соответствует критериям: логин пуст");
        } else if (StringUtils.contains(user.getLogin(), ' ')) {
            log.error("Ошибка валидации пользователя: логин содержит пробелы");
            throw new ValidationException("Пользователь не соответствует критериям: пробелы в логине недопустимы");
        } else if (user.getBirthday().isAfter(LocalDate.now()) || user.getBirthday() == null) {
            log.error("Ошибка валидации пользователя: дата рождения не наступила");
            throw new ValidationException("Пользователь не соответствует критериям: нельзя родиться в будущем");
        }
    }

    @Override
    public void putFriend(int idHost, int idFriend) {
        SqlRowSet userRowsHost = jdbcTemplate.queryForRowSet("select * FROM FRIENDSHIPS as f WHERE f.user_id = ? AND f.friend_id = ?", idHost, idFriend);
        if (!userRowsHost.next()) {
            jdbcTemplate.execute("MERGE INTO FRIENDSHIPS (friendship_id, user_id, friend_id, friendship_status) VALUES ('" + (getCurrentIdFriendships() + 1) + "', '" + idHost + "', '" + idFriend + "', '" + FriendshipStatus.PENDING + "');");
        } else {
            throw new NotFoundException("Такая дружба уже есть");
        }
    }

    @Override
    public void removeFriends(int idHost, int friendId){
        SqlRowSet userRowsHost = jdbcTemplate.queryForRowSet("select * FROM FRIENDSHIPS as f WHERE f.user_id = ? AND f.friend_id = ?", idHost, friendId);
        if (userRowsHost.next()) {
            jdbcTemplate.execute("DELETE FROM FRIENDSHIPS WHERE user_id = '"+idHost+"' AND friend_id = '"+friendId+"';");
        }
    }


}


