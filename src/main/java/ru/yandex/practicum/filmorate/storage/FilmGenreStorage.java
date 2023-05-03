package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;

public interface FilmGenreStorage {

    List<FilmGenre> findByFilmId(Long id);

    FilmGenre findByFilmIdAndGenreId(Long filmId, Long genreId);

    FilmGenre addFilmGenre(FilmGenre filmGenre);

    void deleteByFilmId(Long id);

}
