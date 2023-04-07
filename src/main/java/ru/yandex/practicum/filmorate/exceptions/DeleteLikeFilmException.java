package ru.yandex.practicum.filmorate.exceptions;

public class DeleteLikeFilmException extends RuntimeException {
    public DeleteLikeFilmException(String errorMessage) {
        super(errorMessage);
    }

}
