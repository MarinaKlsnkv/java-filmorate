package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DeleteLikeFilmException;
import ru.yandex.practicum.filmorate.exceptions.LikeFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class FilmService {

    private FilmStorage filmStorage;
    private UserStorage userStorage;

    public void likeFilm(Long id, Long userId) {
        Set<Long> likedFilms = userStorage.getById(userId).getLikedFilms();
        if (likedFilms.contains(id)) {
            log.error("Cannot like film more than 1 time");
            throw new LikeFilmException("Cannot like film more than 1 time");
        }
        Film film = filmStorage.getById(id);
        Long likes = film.getLikes() + 1;
        film.setLikes(likes);
        likedFilms.add(id);
    }

    public void deleteLike(Long id, Long userId) {
        Set<Long> likedFilms = userStorage.getById(userId).getLikedFilms();
        if (!likedFilms.contains(id)) {
            log.error("Film was not likes");
            throw new DeleteLikeFilmException("Film was not likes");
        }
        Film film = filmStorage.getById(id);
        Long likes = film.getLikes() - 1;
        film.setLikes(likes);
        likedFilms.add(id);
    }

    public List<Film> getPopular(Long count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> Long.compare(f2.getLikes(), f1.getLikes()))
                .limit(count)
                .collect(Collectors.toList());
    }

}
