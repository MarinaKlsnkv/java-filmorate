package ru.yandex.practicum.filmorate.exceptions;

public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public FilmNotFoundException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}
