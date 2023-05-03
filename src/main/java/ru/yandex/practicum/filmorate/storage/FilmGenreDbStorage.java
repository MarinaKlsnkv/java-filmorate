package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.UserUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public FilmGenre addFilmGenre(FilmGenre filmGenre) {
        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql,
                filmGenre.getFilmId(),
                filmGenre.getGenreId());
        return filmGenre;
    }

    @Override
    public void deleteByFilmId(Long id) {
        String sqlQuery = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<FilmGenre> findByFilmId(Long id) {
        String sql = "SELECT fg.film_id, fg.genre_id FROM film_genre fg " +
                "JOIN films f ON f.id = fg.film_id " +
                "WHERE f.id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            FilmGenre filmGenre = new FilmGenre();
            filmGenre.setFilmId(rs.getLong("film_id"));
            filmGenre.setGenreId(rs.getLong("genre_id"));
            return filmGenre;
        }, id);
    }

    @Override
    public FilmGenre findByFilmIdAndGenreId(Long filmId, Long genreId) {
        try {
            String sqlQuery = "select film_id, genre_id " +
                    "from film_genre where film_id = ? AND genre_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, new Object[]{filmId, genreId}, (rs, rowNum) -> {
                FilmGenre filmGenre = new FilmGenre();
                filmGenre.setFilmId(rs.getLong("film_id"));
                filmGenre.setGenreId(rs.getLong("genre_id"));
                return filmGenre;
            });
        } catch (Exception e) {
            log.info("Cannot find a record in film_genre with film_id = " + filmId + " , genre_id = " + genreId);
            return null;
        }
    }

    private static class FilmGenreRowMapper implements RowMapper<FilmGenre> {
        @Override
        public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
            FilmGenre filmGenre = new FilmGenre();
            filmGenre.setFilmId(rs.getLong("film_id"));
            filmGenre.setGenreId(rs.getLong("genre_id"));
            return filmGenre;
        }
    }

    private FilmGenre mapRowToFilmGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new FilmGenre(
                resultSet.getLong("film_id"),
                resultSet.getLong("genre_id"));
    }

    private Long simpleSave(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        return simpleJdbcInsert.executeAndReturnKey(UserUtils.userToMap(user)).longValue();
    }
}
