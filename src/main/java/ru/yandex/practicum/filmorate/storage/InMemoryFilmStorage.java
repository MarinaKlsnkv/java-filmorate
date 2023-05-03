package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final ConcurrentHashMap<Long, Film> films = new ConcurrentHashMap<>();
    private final AtomicLong index = new AtomicLong(0);

    @Override
    public Film addFilm(Film film) {
        index.addAndGet(1);
        Long id = index.longValue();
        film.setId(id);
        films.put(id, film);
        return films.get(id);
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Film doesn't exist");
            throw new FilmNotFoundException("Film doesn't exist");
        }
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getById(Long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new FilmNotFoundException("Film with id = " + id + " not found");
        }
        return film;
    }

    @Override
    public Film updateFilmLikes(Film film) {
        throw new UnsupportedOperationException();
    }
}
