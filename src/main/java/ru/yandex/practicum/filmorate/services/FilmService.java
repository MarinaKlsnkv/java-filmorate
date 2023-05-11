package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DeleteLikeFilmException;
import ru.yandex.practicum.filmorate.exceptions.LikeFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmLikeStorage filmLikeStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       FilmLikeStorage filmLikeStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmLikeStorage = filmLikeStorage;
    }

    public void likeFilm(Long id, Long userId) {
        User user = userStorage.getById(userId);
        Set<Long> likedFilms = user.getLikedFilms();
        if (likedFilms.contains(id)) {
            log.error("Cannot like film more than 1 time");
            throw new LikeFilmException("Cannot like film more than 1 time");
        }
        Film film = filmStorage.getById(id);

        Long likes = film.getRate() + 1;
        film.setRate(likes);
        filmStorage.updateFilmLikes(film);
        filmLikeStorage.addLike(new FilmLike(userId, id));
        user.getLikedFilms().add(id);
        userStorage.updateUser(user);

    }

    public void deleteLike(Long id, Long userId) {
        User user = userStorage.getById(userId);
        Set<Long> likedFilms = user.getLikedFilms();
        if (!likedFilms.contains(id)) {
            log.error("Film was not likes");
            throw new DeleteLikeFilmException("Film was not likes");
        }
        Film film = filmStorage.getById(id);
        Long likes = film.getRate() - 1;
        film.setRate(likes);
        filmStorage.updateFilmLikes(film);
        user.getLikedFilms().remove(id);
        userStorage.updateUser(user);
    }

    public List<Film> getPopular(Long count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> Long.compare(f2.getRate(), f1.getRate()))
                .limit(count)
                .collect(Collectors.toList());
    }

}
