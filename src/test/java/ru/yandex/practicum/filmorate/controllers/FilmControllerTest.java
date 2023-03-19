package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private static final String TO_LONG_DESCRIPTION_VALIDATION_ERROR_MSG = "Film description length should be less than 200 symbols";
    private static final String BLANK_FILM_NAME_ERROR_MSG = "Film name shouldn't be blank";
    private static final String NOT_VALID_RELEASE_DATE_VALIDATION_ERROR_MSG = "Film release date should be after 28-DEC-1895";
    private static final String NOT_VALID_FILM_DURATION_ERROR_MSG = "Film duration should be positive";
    private static final long NOT_VALID_FILM_DURATION = -1L;
    private static final String NOT_VALID_FILM_DESCRIPTION = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc, 1";
    private static final Film PREDEFINED_FILM = new Film("Predefined film name", "Predefined film description", LocalDate.now(), 90L);
    private static final LocalDate NOT_VALID_DATE = LocalDate.of(1895, 12, 27);

    private FilmController filmController;

    @BeforeEach
    void init() {
        filmController = new FilmController();
        filmController.addFilm(PREDEFINED_FILM);
    }

    //*****Add Film Tests******//

    @Test
    void addFilmShouldThrowWhenFilmNameIsEmpty() {
        Film film = new Film("", "description", LocalDate.now(),90L);
        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(BLANK_FILM_NAME_ERROR_MSG));
    }

    @Test
    void addFilmShouldThrowWhenDescriptionNameExceeds200() {
        Film film = new Film("name", NOT_VALID_FILM_DESCRIPTION, LocalDate.now(),90L);
        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        String actualMessage = exception.getMessage();

        assertEquals(TO_LONG_DESCRIPTION_VALIDATION_ERROR_MSG, actualMessage);
    }

    @Test
    void addFilmShouldThrowWhenReleaseDateIsBeforeGivenDate() {
        Film film = new Film("name", "description", NOT_VALID_DATE,90L);
        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        String actualMessage = exception.getMessage();

        assertEquals(NOT_VALID_RELEASE_DATE_VALIDATION_ERROR_MSG, actualMessage);
    }

    @Test
    void addFilmShouldThrowWhenFilmDurationIsNegative() {
        Film film = new Film("name", "description", LocalDate.now(), NOT_VALID_FILM_DURATION);
        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        String actualMessage = exception.getMessage();

        assertEquals(NOT_VALID_FILM_DURATION_ERROR_MSG, actualMessage);
    }

    @Test
    void addFilmWorksWithoutErrors() {
        String name = "name";
        Film film = new Film(name, "description", LocalDate.now(), 90L);
        Long id = filmController.addFilm(film).getBody().getId();

        Long actualId = filmController.getFilms().getBody().get(1).getId();
        String actualName = filmController.getFilms().getBody().get(1).getName();
        assertEquals(id, actualId);
        assertEquals(name, actualName);
    }

    //*****Update Film Tests******//
    @Test
    void updateFilmShouldThrowWhenFilmNameIsEmpty() {
        Film film = new Film(1L,"", "description", LocalDate.now(),90L);
        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.updateFilm(film);
        });

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(BLANK_FILM_NAME_ERROR_MSG));
    }

    @Test
    void updateFilmShouldThrowWhenDescriptionNameExceeds200() {
        Film film = new Film(1L, "name", NOT_VALID_FILM_DESCRIPTION, LocalDate.now(),90L);

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.updateFilm(film);
        });

        String actualMessage = exception.getMessage();

        assertEquals(TO_LONG_DESCRIPTION_VALIDATION_ERROR_MSG, actualMessage);
    }

    @Test
    void updateFilmShouldThrowWhenReleaseDateIsBeforeGivenDate() {
        Film film = new Film(1L, "name", "description", NOT_VALID_DATE,90L);

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.updateFilm(film);
        });

        String actualMessage = exception.getMessage();

        assertEquals(NOT_VALID_RELEASE_DATE_VALIDATION_ERROR_MSG, actualMessage);
    }

    @Test
    void updateFilmShouldThrowWhenFilmDurationIsNegative() {
        Film film = new Film(1L, "name", "description", LocalDate.now(), NOT_VALID_FILM_DURATION);

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.updateFilm(film);
        });

        String actualMessage = exception.getMessage();

        assertEquals(NOT_VALID_FILM_DURATION_ERROR_MSG, actualMessage);
    }

    @Test
    void updateFilmWorksWithoutErrors() {
        String name = "name";
        Film film = new Film(1L ,name, "description", LocalDate.now(), 90L);
        filmController.updateFilm(film);

        String actualName = filmController.getFilms().getBody().get(0).getName();
        assertEquals(name, actualName);
    }

    //*****Get Films Tests******//

    @Test
    void getFilmsWorksWithoutErrors() {
        List<Film> expected = new ArrayList<>(List.of(PREDEFINED_FILM));

        List<Film> actual = filmController.getFilms().getBody();

        assertEquals(expected, actual);
    }

}