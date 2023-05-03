package ru.yandex.practicum.filmorate.exceptions;

public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public GenreNotFoundException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}
