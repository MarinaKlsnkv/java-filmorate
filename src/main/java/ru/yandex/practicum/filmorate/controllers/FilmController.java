package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ValidationResult;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;


    public FilmController(@Qualifier("filmDbStorage") FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@RequestBody Film film) {
        ValidationResult validationResult = validate(film);
        if (!validationResult.isValid()) {
            log.error("Not valid film: {}. Errors: {}", film, validationResult.getMessages());
            throw new ValidationException(String.join(", ", validationResult.getMessages()));
        }
        Film savedFilm = filmStorage.addFilm(film);
        log.info("Added new film : {}", savedFilm);
        return ResponseEntity.ok(savedFilm);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {

        ValidationResult validationResult = validate(film);
        if (!validationResult.isValid()) {
            throw new ValidationException(String.join(", ", validationResult.getMessages()));
        }
        Film updatedFilm = filmStorage.updateFilm(film);
        log.info("Updated a film : {}", updatedFilm);
        return ResponseEntity.ok(updatedFilm);
    }

    @GetMapping
    public ResponseEntity<List<Film>> getFilms() {
        List<Film> allFilms = filmStorage.getAllFilms();
        return ResponseEntity.ok(allFilms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable Long id) {
        Film film = filmStorage.getById(id);
        return ResponseEntity.ok(film);
    }

    @PutMapping("{id}/like/{userId}")
    public ResponseEntity<String> likeFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.likeFilm(id, userId);
        return ResponseEntity.ok("Like was added");
    }

    @DeleteMapping("{id}/like/{userId}")
    public ResponseEntity<String> deleteLikeFromFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
        return ResponseEntity.ok("Like was deleted");
    }

    @GetMapping("popular")
    public ResponseEntity<List<Film>> getPopular(@RequestParam(required = false) Long count) {
        List<Film> popularFilms;
        if (count != null) {
            popularFilms = filmService.getPopular(count);
        } else {
            popularFilms = filmService.getPopular(10L);
        }
        return ResponseEntity.ok(popularFilms);
    }

    private ValidationResult validate(Film film) {
        ValidationResult validationResult = new ValidationResult(true, new ArrayList<>());
        List<String> messages = validationResult.getMessages();
        if (film.getName().isBlank()) {
            validationResult.setValid(false);
            messages.add("Film name shouldn't be blank");
        }
        if (film.getDescription().length() > 200) {
            validationResult.setValid(false);
            messages.add("Film description length should be less than 200 symbols");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            validationResult.setValid(false);
            messages.add("Film release date should be after 28-DEC-1895");
        }
        if (film.getDuration() <= 0) {
            validationResult.setValid(false);
            messages.add("Film duration should be positive");
        }

        return validationResult;
    }

}
