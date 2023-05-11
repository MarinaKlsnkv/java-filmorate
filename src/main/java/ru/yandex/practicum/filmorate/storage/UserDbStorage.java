package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.UserUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final JdbcTemplate jdbcTemplate;
    private final FriendshipStorage friendshipStorage;
    private final FilmLikeStorage filmLikeStorage;

    public UserDbStorage(JdbcTemplate jdbcTemplate, FriendshipStorage friendshipStorage, FilmLikeStorage filmLikeStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendshipStorage = friendshipStorage;
        this.filmLikeStorage = filmLikeStorage;
    }

    @Override
    public User addUser(User user) {
        user.setId(simpleSave(user));
        return user;
    }

    @Override
    public User updateUser(User user) {
        User checkUser = getById(user.getId());
        if (checkUser != null) {
            String sqlQuery = "update users set " +
                    "email = ?, login = ?, name = ?, birthday = ? " +
                    "where id = ?";

            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday().toString(),
                    user.getId());
            return user;
        } else {
            throw new UserNotFoundException("User with id = " + user.getId() + " not found");
        }
    }

    @Override
    public List<User> getAllUsers() {
        //todo likes and friends?
        String sqlQuery = "select id, email, login, name, birthday from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getById(Long id) {
        try {
            String sqlQuery = "select id, email, login, name, birthday " +
                    "from users where id = ?";
            User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
            List<Friendship> allFriendships = friendshipStorage.findFriendsByUserId(id);
            user.getFriends().addAll(allFriendships);
            List<FilmLike> likes = filmLikeStorage.findByUserId(id);
            Set<Long> likedFilmsIds = likes.stream().map(l -> l.getFilmId()).collect(Collectors.toSet());
            user.getLikedFilms().addAll(likedFilmsIds);
            return user;
        } catch (Exception e) {
            throw new UserNotFoundException("User with id = " + id + " not found", e);
        }
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                LocalDate.parse(resultSet.getString("birthday"), dateFormatter)
        );
    }

    private Long simpleSave(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        return simpleJdbcInsert.executeAndReturnKey(UserUtils.userToMap(user)).longValue();
    }
}
