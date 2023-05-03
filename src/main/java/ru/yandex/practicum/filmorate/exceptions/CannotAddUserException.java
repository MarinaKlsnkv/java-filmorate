package ru.yandex.practicum.filmorate.exceptions;

public class CannotAddUserException extends RuntimeException {
    public CannotAddUserException(String errorMessage) {
        super(errorMessage);
    }

}
