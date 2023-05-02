package ru.yandex.practicum.filmorate.controllers.exceptionhandler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.yandex.practicum.filmorate.exceptions.DeleteLikeFilmException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.LikeFilmException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value
            = {UserNotFoundException.class, FilmNotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(
            RuntimeException ex, WebRequest request) {
        ErrorResponse bodyOfResponse = new ErrorResponse("Resource not found");

        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value
            = {ValidationException.class})
    protected ResponseEntity<Object> handleNotValid(
            RuntimeException ex, WebRequest request) {
        ErrorResponse bodyOfResponse = new ErrorResponse("Not valid input");

        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value
            = {LikeFilmException.class})
    protected ResponseEntity<Object> handleCannotLikeFilmTwice(
            RuntimeException ex, WebRequest request) {
        ErrorResponse bodyOfResponse = new ErrorResponse("Cannot like film twice");

        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value
            = {DeleteLikeFilmException.class})
    protected ResponseEntity<Object> handleCannotDeleteLikeFromUnlikedFilm(
            RuntimeException ex, WebRequest request) {
        ErrorResponse bodyOfResponse = new ErrorResponse("This film was not liked by the user. Like was not removed");

        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}
