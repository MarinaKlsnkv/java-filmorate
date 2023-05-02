package ru.yandex.practicum.filmorate.exceptions;

public class LikeFilmException extends RuntimeException {
    public LikeFilmException(String errorMessage) {
        super(errorMessage);
    }

}
