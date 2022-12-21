package ru.yandex.practicum.filmorate.storage.user;

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
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

@Component
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Integer, User> getUsers() {
        Map<Integer, User> userMap = new HashMap<>();
        String sqlQuery = "SELECT * FROM USERS as u LEFT OUTER JOIN FRIENDSHIPS as f ON u.USER_ID = f.USER_ID;";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery);
        while (userRows.next()) {
            User user = createUserFromSQL(userRows);
            log.info("Найден пользователь: {} {}", user.getId(), user.getEmail());
            userMap.put(user.getId(), user);
        }
        return userMap;
    }


    @Override
    public User getUserById(Integer id) {
        String sqlQuery = "SELECT * FROM USERS as u WHERE u.USER_ID = ?;";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRows.next()) {
            log.info("Пользователь с идентификатором {} найден.", id);
            return createUserFromSQL(userRows);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return null;
        }
    }

    @Override
    public List<User> getUsersFriends(Integer id) {
        String sqlQuery = "SELECT * FROM FRIENDSHIPS as f LEFT JOIN USERS as u ON f.FRIEND_ID=u.USER_ID " +
                "WHERE f.USER_ID = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new User(
                rs.getInt("FRIEND_ID"),
                rs.getString("EMAIL"),
                rs.getString("LOGIN"),
                rs.getString("NAME"),
                rs.getDate("BIRTHDAY_DATE").toLocalDate(),
                new ArrayList<>()
        ), id);
    }

    public User createUserFromSQL(SqlRowSet userRows) {
        int id = userRows.getInt("user_id");
        User user = new User(
                id,
                userRows.getString("email"),
                userRows.getString("login"),
                userRows.getString("name"),
                userRows.getDate("birthday_date").toLocalDate(),
                new ArrayList<>());
        user.setFriendship(getUsersFriends(id));
        return user;
    }

    @Override
    public User postUsers(User user) {
        userValidate(user);
        String sqlQuery = "SELECT * FROM USERS as u LEFT OUTER JOIN FRIENDSHIPS as f ON u.USER_ID = f.USER_ID " +
                "WHERE u.EMAIL = ?;";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, user.getEmail());
        if (!userRows.next()) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            String sqlQueryPost = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY_DATE) VALUES (?, ?, ? ,?);";
            jdbcTemplate.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(sqlQueryPost, new String[]{"USER_ID"});
                statement.setString(1, user.getEmail());
                statement.setString(2, user.getLogin());
                statement.setString(3, user.getName());
                statement.setDate(4, Date.valueOf(user.getBirthday()));
                return statement;
            }, keyHolder);
            user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
            return getUserById(user.getId());
        } else {
            throw new NotFoundException("Такой пользователь уже имеется");
        }
    }

    @Override
    public User putUsers(User user) {
        userValidate(user);
        System.out.println("!!!!!!!!!!!! " + user.getId());
        String sqlQuery = "SELECT * FROM USERS WHERE USER_ID = ?;";
        SqlRowSet usersRow = jdbcTemplate.queryForRowSet(sqlQuery, user.getId());
        if (usersRow.next()) {
            String sqlQueryPut = "UPDATE USERS SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY_DATE = ? WHERE USER_ID = ?;";
            jdbcTemplate.update(sqlQueryPut, user.getEmail(), user.getLogin(),
                    user.getName(), user.getBirthday(), user.getId());
        } else {
            postUsers(user);
        }
        return getUserById(user.getId());
    }

    @Override
    public void deleteUsers(User user) {
        String sqlQuery = "SELECT * FROM USERS WHERE USER_ID = ?;";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, user.getId());
        if (userRows.next()) {
            String sqlQueryDel = "DELETE FROM USERS WHERE USER_ID = ?;";
            jdbcTemplate.update(sqlQueryDel, user.getId());
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
        String sqlQuery = "SELECT * FROM FRIENDSHIPS as f WHERE f.USER_ID = ? AND f.FRIEND_ID = ?;";
        SqlRowSet friendsRow = jdbcTemplate.queryForRowSet(sqlQuery, idHost, idFriend);
        if (!friendsRow.next()) {
            String sqlQueryPutFriend = "INSERT INTO FRIENDSHIPS (USER_ID, FRIEND_ID, FRIENDSHIP_STATUS) " +
                    "VALUES (?, ?, ?);";
            jdbcTemplate.update(sqlQueryPutFriend, idHost, idFriend, "PENDING");
        } else {
            throw new NotFoundException("Такая дружба уже есть");
        }
    }

    @Override
    public void removeFriends(int idHost, int idFriend) {
        String sqlQuery = "SELECT * FROM FRIENDSHIPS as f WHERE f.USER_ID = ? AND f.FRIEND_ID = ?;";
        SqlRowSet friendsRow = jdbcTemplate.queryForRowSet(sqlQuery, idHost, idFriend);
        if (friendsRow.next()) {
            String sqlQueryDel = "DELETE FROM FRIENDSHIPS WHERE USER_ID = ? AND FRIEND_ID = ?;";
            jdbcTemplate.update(sqlQueryDel, idHost, idFriend);
        }
    }


}


