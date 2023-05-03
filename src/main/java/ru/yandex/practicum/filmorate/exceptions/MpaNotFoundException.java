package ru.yandex.practicum.filmorate.exceptions;

public class MpaNotFoundException extends RuntimeException {
    public MpaNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public MpaNotFoundException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}
