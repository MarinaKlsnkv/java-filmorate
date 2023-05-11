package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
public class FilmLikeStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmLikeStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<FilmLike> findByUserId(Long userId) {
        String sql = "SELECT * FROM film_likes WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) -> {
            FilmLike filmLike = new FilmLike();
            filmLike.setUserId(rs.getLong("user_id"));
            filmLike.setFilmId(rs.getLong("film_id"));
            return filmLike;
        });
    }

    private Friendship mapRowToFriendship(ResultSet resultSet, int rowNum) throws SQLException {
        return new Friendship(
                resultSet.getLong("user_id"),
                resultSet.getLong("friend_id"),
                resultSet.getBoolean("confirmed")
        );
    }

    public void addLike(FilmLike filmLike) {
        String sql = "INSERT INTO film_likes (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmLike.getUserId(), filmLike.getFilmId());
    }

    public void deleteLike(Long userId, Long filmId) {
        String sql = "DELETE FROM film_likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
    }

}
