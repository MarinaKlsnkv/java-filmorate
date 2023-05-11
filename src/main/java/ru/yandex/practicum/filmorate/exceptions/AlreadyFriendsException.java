package ru.yandex.practicum.filmorate.exceptions;

public class AlreadyFriendsException extends RuntimeException {
    public AlreadyFriendsException(String errorMessage) {
        super(errorMessage);
    }

    public AlreadyFriendsException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}
