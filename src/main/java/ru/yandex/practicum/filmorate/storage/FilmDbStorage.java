package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.utils.FilmUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository("filmDbStorage")
@Slf4j
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final FilmGenreStorage filmGenreStorage;
    private final GenreStorage genreStorage;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        film.setId(simpleSave(film));
        removeGenreDuplicates(film);
        persistGenres(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Film checkFilm = getById(film.getId());
        if (checkFilm != null) {
            removeGenreDuplicates(film);
            persistGenres(film);
            String sqlQuery = "update films set " +
                    "name = ?, description = ?, releaseDate = ?, duration = ?, rate = ?, mpa_id = ? " +
                    "where id = ?";

            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate().toString(),
                    film.getDuration(),
                    film.getRate(),
                    film.getMpa().getId(),
                    film.getId());

            return film;
        } else {
            throw new UserNotFoundException("Film with id = " + film.getId() + " not found");
        }
    }

    @Override
    public Film updateFilmLikes(Film film) {
        Film checkFilm = getById(film.getId());
        if (checkFilm != null) {
            String sqlQuery = "update films set " +
                    "name = ?, description = ?, releaseDate = ?, duration = ?, rate = ?, mpa_id = ? " +
                    "where id = ?";

            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate().toString(),
                    film.getDuration(),
                    film.getRate(),
                    film.getMpa().getId(),
                    film.getId());
            return film;
        } else {
            throw new UserNotFoundException("Film with id = " + film.getId() + " not found");
        }
    }

    private void removeGenreDuplicates(Film film) {
        List<Genre> genresList = film.getGenres();
        var genres = new LinkedHashSet<>(genresList);
        var genresCleaned = new ArrayList<>(genres);
        film.setGenres(genresCleaned);
    }

    private void persistGenres(Film film) {
        List<Genre> genres = film.getGenres();
        if (!genres.isEmpty()) {
            filmGenreStorage.deleteByFilmId(film.getId());

            for (Genre genre : genres) {
                filmGenreStorage.addFilmGenre(new FilmGenre(film.getId(), genre.getId()));
            }
        } else {
            filmGenreStorage.deleteByFilmId(film.getId());
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "select films.id, films.name, films.description, films.releaseDate, films.duration, " +
                "films.rate, mpa_rating.id, mpa_rating.name  from films " +
                "left join mpa_rating on films.mpa_id = mpa_rating.id ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilmJoinMpa);
    }

    @Override
    public Film getById(Long id) {
        try {
            String sqlQuery = "select films.id, films.name, films.description, films.releaseDate, films.duration, " +
                    "films.rate, mpa_rating.id, mpa_rating.name  from films " +
                    "left join mpa_rating on films.mpa_id = mpa_rating.id where films.id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilmJoinMpa, id);
        } catch (Exception e) {
            log.error("Film not found. Cause : {}", e);
            throw new FilmNotFoundException("Film with id = " + id + " not found", e);
        }
    }

    private Film mapRowToFilmJoinMpa(ResultSet resultSet, int rowNum) throws SQLException {
        String ratingName = resultSet.getString("mpa_rating.name");
        Long ratingId = resultSet.getLong("mpa_rating.id");

        long filmId = resultSet.getLong("films.id");
        List<FilmGenre> filmGenres = filmGenreStorage.findByFilmId(filmId);
        Set<Genre> genres = filmGenres.stream().map(g -> genreStorage.getById(g.getGenreId())).collect(Collectors.toSet());

        Film film = new Film(
                filmId,
                resultSet.getString("films.name"),
                resultSet.getString("films.description"),
                LocalDate.parse(resultSet.getString("films.releaseDate"), dateFormatter),
                resultSet.getLong("films.duration"),
                resultSet.getLong("films.rate"),
                new MpaRating(ratingId, ratingName));
        film.getGenres().addAll(genres);
        return film;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        //todo add genres and mpa
        //todo likes?

        return new Film(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                LocalDate.parse(resultSet.getString("releaseDate"), dateFormatter),
                resultSet.getLong("duration"));
    }

    private Long simpleSave(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        return simpleJdbcInsert.executeAndReturnKey(FilmUtils.filmToMap(film)).longValue();
    }
}
