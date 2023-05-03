package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MpaRating> getAll() {
        String sqlQuery = "select * from mpa_rating";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public MpaRating getById(Long id) {
        try {
            String sqlQuery = "select * " +
                    "from mpa_rating where id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id);
        } catch (Exception e) {
            throw new MpaNotFoundException("Mpa with id = " + id + " not found", e);
        }
    }

    private MpaRating mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return new MpaRating(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
    }

}
