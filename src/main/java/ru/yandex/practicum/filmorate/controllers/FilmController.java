package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ValidationResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final ConcurrentHashMap<Long, Film> films = new ConcurrentHashMap<>();
    private final AtomicLong index = new AtomicLong(0);

    @PostMapping
    public ResponseEntity<Film> addFilm(@RequestBody Film film) {
        ValidationResult validationResult = validate(film);
        if (!validationResult.isValid()) {
            log.error("Not valid film: {}. Errors: {}", film, validationResult.getMessages());
            throw new ValidationException(String.join(", ", validationResult.getMessages()));
        }
        index.addAndGet(1);
        Long id = index.longValue();
        film.setId(id);
        films.put(id, film);
        Film savedFilm = films.get(id);
        log.info("Added new film : {}", savedFilm);
        return ResponseEntity.ok(savedFilm);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("User doesn't exist");
        };
        ValidationResult validationResult = validate(film);
        if (!validationResult.isValid()) {
            throw new ValidationException(String.join(", ", validationResult.getMessages()));
        }
        films.put(film.getId(), film);
        Film updatedFilm = films.get(film.getId());
        log.info("Updated a film : {}", updatedFilm);
        return ResponseEntity.ok(updatedFilm);
    }

    @GetMapping
    public ResponseEntity<List<Film>> getFilms() {
        List<Film> allFilms = new ArrayList<>();
        allFilms.addAll(films.values());
        return ResponseEntity.ok(allFilms);
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
        if (film.getDuration() <= 0){
            validationResult.setValid(false);
            messages.add("Film duration should be positive");
        }

        return validationResult;
    }

}
